create table productos
(
    id              bigint auto_increment
        primary key,
    activo          bit                                                                               not null,
    codigo_barras   varchar(50)                                                                       not null,
    descripcion     varchar(255)                                                                      not null,
    fecha_caducidad date                                                                              null,
    imagen          varchar(255)                                                                      null,
    nombre          varchar(255)                                                                      not null,
    peso            decimal(5, 2)                                                                     not null,
    precio          decimal(10, 2)                                                                    not null,
    stock           int                                                                               not null,
    unidad_medida   enum ('CENTIMETRO', 'GRAMO', 'KILOGRAMO', 'LITRO', 'METRO', 'MILILITRO', 'PIEZA') not null,
    valoracion      decimal(2, 1)                                                                     not null,
    constraint UKb2vi4382tjm9b8q2sgfiox2ec
        unique (codigo_barras),
    check (`stock` >= 0)
);

create table promociones
(
    id                   bigint auto_increment
        primary key,
    activa               bit           not null,
    fecha_fin            date          null,
    fecha_inicio         date          not null,
    nombre               varchar(100)  not null,
    porcentaje_descuento decimal(5, 2) not null
);

create table promociones_productos
(
    producto_id  bigint not null,
    promocion_id bigint not null,
    primary key (producto_id, promocion_id),
    constraint FK2irikw0y9d8q96rf1fvm01w5u
        foreign key (promocion_id) references promociones (id),
    constraint FKsblem1x35k5et58jyxm995628
        foreign key (producto_id) references productos (id)
);

create table proveedores
(
    id        bigint auto_increment
        primary key,
    activo    bit          not null,
    direccion varchar(250) null,
    email     varchar(150) null,
    nombre    varchar(150) not null,
    telefono  varchar(20)  null
);

create table abastecimientos
(
    id           bigint auto_increment
        primary key,
    fecha        datetime(6)    not null,
    total        decimal(10, 2) not null,
    proveedor_id bigint         not null,
    constraint FKr227mhda6u4lioxeh3dhv1lmw
        foreign key (proveedor_id) references proveedores (id)
);

create table detalles_abastecimiento
(
    id                bigint auto_increment
        primary key,
    cantidad          int            not null,
    costo_unitario    decimal(10, 2) not null,
    subtotal          decimal(10, 2) not null,
    abastecimiento_id bigint         not null,
    producto_id       bigint         not null,
    constraint FK1sqfbfk52bt4p1mh3vonshndl
        foreign key (producto_id) references productos (id),
    constraint FKmtoy9rw2o53ci76nmk8jntef
        foreign key (abastecimiento_id) references abastecimientos (id),
    check (`cantidad` >= 1)
);

create table usuarios
(
    id         bigint auto_increment
        primary key,
    apellido   varchar(255)                                not null,
    contrasena varchar(255)                                not null,
    email      varchar(255)                                not null,
    nombre     varchar(255)                                not null,
    rol        enum ('ADMINISTRADOR', 'CAJERO', 'CLIENTE') not null,
    constraint UKkfsp0s1tflm1cwlj8idhqsad0
        unique (email)
);

create table administradores
(
    numero_administrador bigint not null,
    id                   bigint not null
        primary key,
    constraint UKou28wtd9lrjjyuh4hm86eai1n
        unique (numero_administrador),
    constraint FKk8pfcp9y41epaar30iqjcgy6u
        foreign key (id) references usuarios (id)
);

create table cajeros
(
    caja_asignada   bigint                                      not null,
    numero_empleado bigint                                      not null,
    turno           enum ('MATUTINO', 'NOCTURNO', 'VESPERTINO') not null,
    id              bigint                                      not null
        primary key,
    constraint UKo75fon8eo41xuxu7vp5h59bhd
        unique (numero_empleado),
    constraint FKdyaodpysyuor5nm9mfisroo99
        foreign key (id) references usuarios (id)
);

create table clientes
(
    nivel_lealtad enum ('BRONCE', 'ORO', 'PLATA') not null,
    puntos        int                             not null,
    id            bigint                          not null
        primary key,
    constraint FKl8la8bmm5w5vtntkh5bhv5y41
        foreign key (id) references usuarios (id),
    check (`puntos` >= 0)
);

create table ventas
(
    id                   bigint auto_increment
        primary key,
    descuento_por_puntos decimal(10, 2)                                        not null,
    fecha                datetime(6)                                           not null,
    metodo_pago          enum ('EFECTIVO', 'OTRO', 'TARJETA', 'TRANSFERENCIA') not null,
    puntos_ganados       int                                                   not null,
    puntos_utilizados    int                                                   not null,
    total                decimal(10, 2)                                        not null,
    cajero_id            bigint                                                not null,
    cliente_id           bigint                                                null,
    constraint FK4dgjhccl2uuo8swmxlxb4ipb5
        foreign key (cliente_id) references clientes (id),
    constraint FKdc3w6cyp0uijo9k0sb70d7ad0
        foreign key (cajero_id) references cajeros (id),
    check (`puntos_ganados` >= 0),
    check (`puntos_utilizados` >= 0)
);

create table detalles_venta
(
    id                   bigint auto_increment
        primary key,
    cantidad             int            not null,
    descuento_importe    decimal(10, 2) not null,
    descuento_porcentaje decimal(5, 2)  not null,
    impuesto_importe     decimal(10, 2) not null,
    precio_unitario      decimal(10, 2) not null,
    subtotal             decimal(10, 2) not null,
    total                decimal(10, 2) not null,
    producto_id          bigint         not null,
    promocion_id         bigint         null,
    venta_id             bigint         not null,
    constraint uk_detalle_venta_producto
        unique (venta_id, producto_id),
    constraint FK453xcyfk9n6snv6qnjlo0p65p
        foreign key (venta_id) references ventas (id),
    constraint FK7mddblufrocsknhbs4kjtlx94
        foreign key (producto_id) references productos (id),
    constraint FK8o33e4b7c5g6cw3sfgx9w8777
        foreign key (promocion_id) references promociones (id),
    check (`cantidad` >= 1)
);

