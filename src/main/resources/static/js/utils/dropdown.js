function initSearchSelect(config) {
    const {
        wrapperId,
        dataId,
        inputId,
        inputRowId,
        dropdownId,
        hiddenFieldsId,
        fieldName,
        badgeText = '',
        multiple = false,
        variant = 'search'
    } = config;

    const wrapper = document.getElementById(wrapperId);
    const data = document.getElementById(dataId);
    const input = document.getElementById(inputId);
    const inputRow = document.getElementById(inputRowId);
    const dropdown = document.getElementById(dropdownId);
    const hiddenFields = document.getElementById(hiddenFieldsId);

    if (!wrapper || !data || !input || !inputRow || !dropdown || !hiddenFields) {
        return;
    }

    const items = Array.from(data.children)
        .map(element => ({
            id: element.dataset.id,
            nombre: element.dataset.nombre
        }))
        .sort((a, b) => a.nombre.localeCompare(b.nombre, 'es'));

    let selected = [];
    let filtered = [...items];
    let focusedIndex = -1;

    wrapper.classList.add(`ss-${variant}`);
    if (multiple) {
        wrapper.classList.add('multiple');
    }

    const arrow = document.createElement('span');
    arrow.className = 'ss-arrow';
    arrow.setAttribute('aria-hidden', 'true');
    inputRow.appendChild(arrow);

    function setOpen(open) {
        dropdown.classList.toggle('open', open);
        wrapper.classList.toggle('enfocado', open);
        input.setAttribute('aria-expanded', String(open));
    }

    function updateLabel() {
        wrapper.classList.toggle('has-value', selected.length > 0 || input.value.trim() !== '');
    }

    function updateHiddenFields() {
        hiddenFields.replaceChildren(...selected.map(item => {
            const field = document.createElement('input');
            field.type = 'hidden';
            field.name = fieldName;
            field.value = item.id;
            return field;
        }));
    }

    function renderTags() {
        inputRow.querySelectorAll('.ss-tag').forEach(tag => tag.remove());

        if (!multiple) {
            return;
        }

        selected.forEach(item => {
            const tag = document.createElement('span');
            tag.className = 'ss-tag';
            tag.textContent = item.nombre;

            const remove = document.createElement('button');
            remove.type = 'button';
            remove.className = 'ss-tag-remove';
            remove.setAttribute('aria-label', `Quitar ${item.nombre}`);
            remove.textContent = '×';
            remove.addEventListener('click', () => toggleOption(item));

            tag.appendChild(remove);
            inputRow.insertBefore(tag, input);
        });
    }

    function renderDropdown() {
        const query = input.value.trim().toLocaleLowerCase('es');
        filtered = items.filter(item => item.nombre.toLocaleLowerCase('es').includes(query));
        focusedIndex = -1;
        dropdown.replaceChildren();

        if (filtered.length === 0) {
            const empty = document.createElement('div');
            empty.className = 'ss-empty';
            empty.textContent = 'Sin resultados';
            dropdown.appendChild(empty);
            return;
        }

        filtered.forEach((item, index) => {
            const option = document.createElement('div');
            const isSelected = selected.some(selectedItem => selectedItem.id === item.id);
            option.className = `ss-option${isSelected ? ' selected' : ''}`;
            option.dataset.index = String(index);
            option.setAttribute('role', 'option');
            option.setAttribute('aria-selected', String(isSelected));

            const name = document.createElement('span');
            name.className = 'ss-opt-name';
            name.textContent = item.nombre;
            option.appendChild(name);

            if (badgeText) {
                const badge = document.createElement('span');
                badge.className = 'ss-opt-badge';
                badge.textContent = badgeText;
                option.appendChild(badge);
            }

            if (isSelected) {
                const check = document.createElement('span');
                check.className = 'ss-opt-check';
                check.setAttribute('aria-hidden', 'true');
                check.textContent = '✓';
                option.appendChild(check);
            }

            option.addEventListener('mousedown', event => {
                event.preventDefault();
                toggleOption(item);
            });
            dropdown.appendChild(option);
        });
    }

    function toggleOption(item) {
        if (!multiple) {
            selected = [item];
            input.value = item.nombre;
            setOpen(false);
        } else {
            const selectedIndex = selected.findIndex(selectedItem => selectedItem.id === item.id);
            if (selectedIndex === -1) {
                selected.push(item);
            } else {
                selected.splice(selectedIndex, 1);
            }
            input.value = '';
            input.focus();
        }

        renderTags();
        updateHiddenFields();
        updateLabel();
        renderDropdown();
    }

    function openDropdown() {
        renderDropdown();
        setOpen(true);
    }

    input.setAttribute('role', 'combobox');
    input.setAttribute('aria-autocomplete', 'list');
    input.setAttribute('aria-expanded', 'false');
    input.addEventListener('focus', openDropdown);
    input.addEventListener('input', openDropdown);
    input.addEventListener('blur', () => {
        wrapper.classList.remove('enfocado');
        updateLabel();
    });

    input.addEventListener('keydown', event => {
        const options = dropdown.querySelectorAll('.ss-option');

        if (event.key === 'ArrowDown' && options.length > 0) {
            event.preventDefault();
            focusedIndex = Math.min(focusedIndex + 1, options.length - 1);
        } else if (event.key === 'ArrowUp' && options.length > 0) {
            event.preventDefault();
            focusedIndex = Math.max(focusedIndex - 1, 0);
        } else if (event.key === 'Enter' && focusedIndex >= 0) {
            event.preventDefault();
            toggleOption(filtered[focusedIndex]);
            return;
        } else if (event.key === 'Escape') {
            event.preventDefault();
            if (!multiple && selected.length > 0) {
                input.value = selected[0].nombre;
            }
            setOpen(false);
            updateLabel();
            return;
        } else {
            return;
        }

        options.forEach((option, index) => {
            option.classList.toggle('focused', index === focusedIndex);
        });
    });

    document.addEventListener('mousedown', event => {
        if (!wrapper.contains(event.target)) {
            setOpen(false);
        }
    });

    renderTags();
    updateHiddenFields();
    updateLabel();
    renderDropdown();
}
