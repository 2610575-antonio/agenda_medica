<%-- 
   CREATE DATABASE IF NOT EXISTS agenda_medica
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE agenda_medica

create table ususarios (
id INT AUTO_INCREMENT PRIMARY KEY,
usuario VARCHAR(50) NOT NULL UNIQUE,
password VARCHAR(255) NOT NULL not null,
nombre varchar(100) not null,
rol varchar(20) not null default 'recepcion',
activo boolean not null default true,
ficha_creacion timestamp default current_timestamp
) engine=InnoDB;




create table medicos (
     id INT AUTO_INCREMENT PRIMARY KEY,
     usuario_id int unique,
     cedula_profecional varchar(30) not null unique,
     nombre varchar(100) not null,
     apellido_paterno varchar(100) not null,
	 apellido_materno varchar(100),
     especialidad varchar(100) not null,
     telefono varchar(20),
     correo varchar(150),
     activo boolean not null default true,
     
     foreign key (usuario_id)
		 references usuarios(id)
         on delete set null
         on update cascade
)engine=InnoDB;

  create table pacientes (
      id INT AUTO_INCREMENT PRIMARY KEY,
      nombre varchar(100) not null,
      apellido_paterno varchar(100) not null,
	  apellido_materno varchar(100),
      fecha_nacimiento date,
      sexo varchar(20),
      telefono varchar(20),
      correo varchar(150),
      domicilio varchar(250),
      contacto_emergencia varchar(150),
      telefono_emergencia varchar(20),
      fecha_registro timestamp default current_timestamp,
      activo boolean not null default true
)engine=InnoDB;
     
     
create table consultorios (
	id INT AUTO_INCREMENT PRIMARY KEY,
	nombre varchar(100) not null,
    ubicacion varchar(150),
	activo boolean not null default true
)engine=InnoDB;
     
     
     
create table citas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    paciente_id int not null,
    medico_id int not null,
    consultorio_id int,
    fecha date not null,
    hora time not null,
    motivo varchar(250),
    estado varchar(20) not null default 'programada',
    observaciones varchar(500),
    creado_por int,
	fecha_registro timestamp default current_timestamp,

    foreign key (paciente_id)
		references pacientes(id)
        on delete set null
		on update cascade,
        
    foreign key (medico_id)
		references medicos(id)
        on delete set null
		on update cascade,    
        
    foreign key (consultorio_id)
		references consultorios(id)
        on delete set null
		on update cascade,    
        
    foreign key (creado_por)
		references usuarios(id)
        on delete set null
		on update cascade,    
)engine=InnoDB;
        
create table expedientes (
	id INT AUTO_INCREMENT PRIMARY KEY,
    paciente_id int not null,
    password_hash varchar(255) not null,
    antecedentes_medicos text,
    alergias text,
    medicamentos_actuales text,
    antecedentes_familiares text,
    cirugias text,
    enfermedades_cronicas text,
	fecha_registro timestamp default current_timestamp,
    fecha_actualizacion timestamp default current_timestamp
		on update current_timestamp,
	activo boolean not null default true,
    
    foreign key (paciente_id)
		references paciente(id)
	    on delete set null
		on update cascade,
)engine=InnoDB;

create table consultas (
	id INT AUTO_INCREMENT PRIMARY KEY,
    cita_id int,
    paciente_id int not null,
    medico_id int not null,
    fecha_consulta datetime not null default current_timestamp,
    motivo_consulta text,
    signos_vitales text,
    diagnostico text,
    tratamiento text,
    observaciones text,
    proxima_cita date,
    fecha_registro timestamp default current_timestamp,
    
    foreign key (citas_id)
        references citas(id)
        on delete set null
		on update cascade,
        
        foreign key (paciente_id)
		references pacientes(id)
        on delete set null
		on update cascade,
        
    foreign key (medico_id)
		references medicos(id)
        on delete set null
		on update cascade,
) engine=InnoDB;

create table accesos_expediente (
	id INT AUTO_INCREMENT PRIMARY KEY,
    expediente_id int not null,
    usuario_id int,
    fecha_hora timestamp default current_timestamp,
    resultado varchar(20) not null,
    descripcion varchar(250),
    
    foreign key (expediente_id)
		references expedientes(id)
        on delete set null
		on update cascade,

    foreign key (usuario_id)
		 references usuarios(id)
         on delete set null
         on update cascade    
) engine=InnoDB;


use agenda_medica;

show tables;
--%>


