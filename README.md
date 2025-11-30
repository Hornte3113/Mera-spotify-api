# Backend Catálogo de Música - Ktor + PostgreSQL

Backend para gestión de catálogo musical desarrollado con Ktor y PostgreSQL.

## Características

- 3 Entidades relacionadas: Artistas → Álbumes → Tracks
- 12 Endpoints REST completos (4 operaciones CRUD por entidad)
- Protección contra borrado en cascada
- Códigos de estado HTTP correctos (200, 201, 404, 409, 500)
- Arquitectura modular
- Validaciones de relaciones entre entidades

## Tecnologías

- Kotlin 2.2.20
- Ktor 3.3.2
- PostgreSQL 14+
- Exposed ORM 0.57.0
- HikariCP 5.1.0

## Estructura del Proyecto

```
src/main/kotlin/
├── Application.kt          # Punto de entrada y configuración principal
├── Routing.kt             # Configuración de rutas
├── Serialization.kt       # Configuración de JSON
├── database/
│   └── DatabaseFactory.kt # Conexión y pool de base de datos
├── models/
│   ├── Artista.kt        # Modelo y tabla de Artistas
│   ├── Album.kt          # Modelo y tabla de Álbumes
│   └── Track.kt          # Modelo y tabla de Tracks
├── services/
│   ├── ArtistaService.kt # Lógica de negocio de Artistas
│   ├── AlbumService.kt   # Lógica de negocio de Álbumes
│   └── TrackService.kt   # Lógica de negocio de Tracks
└── routes/
    ├── ArtistaRoutes.kt  # Endpoints REST de Artistas
    ├── AlbumRoutes.kt    # Endpoints REST de Álbumes
    └── TrackRoutes.kt    # Endpoints REST de Tracks
```

## Instalación y Configuración

### Paso 1: Configurar Conexión a la Base de Datos

Por defecto, el proyecto se conecta con:
- **Host**: localhost
- **Puerto**: 5432
- **Base de datos**: musica_db
- **Usuario**: postgres
- **Contraseña**: postgres

tambien se puede:

**Opción A**: Establecer variable de entorno `DATABASE_URL`:
```powershell
$env:DATABASE_URL="jdbc:postgresql://localhost:5432/musica_db?user=TU_USUARIO&password=TU_PASSWORD"
```

### Paso 2: Compilar y Ejecutar

1. El servidor iniciará en: `http://localhost:3000`

## API Endpoints

### Base URL: `http://localhost:3000/api`

### Artistas

| Método | Endpoint | Descripción | Código Éxito |
|--------|----------|-------------|--------------|
| POST | `/artistas` | Crear artista | 201 |
| GET | `/artistas` | Listar todos los artistas | 200 |
| GET | `/artistas/{id}` | Obtener artista por ID | 200 |
| PUT | `/artistas/{id}` | Actualizar artista | 200 |
| DELETE | `/artistas/{id}` | Eliminar artista | 200 |

*No se puede eliminar un artista con álbumes asociados (devuelve 409 Conflict)

### Álbumes

| Método | Endpoint | Descripción | Código Éxito |
|--------|----------|-------------|--------------|
| POST | `/albumes` | Crear álbum | 201 |
| GET | `/albumes` | Listar todos los álbumes | 200 |
| GET | `/albumes/{id}` | Obtener álbum por ID | 200 |
| PUT | `/albumes/{id}` | Actualizar álbum | 200 |
| DELETE | `/albumes/{id}` | Eliminar álbum | 200 |

*No se puede eliminar un álbum con tracks asociados (devuelve 409 Conflict)

### Tracks

| Método | Endpoint | Descripción | Código Éxito |
|--------|----------|-------------|--------------|
| POST | `/tracks` | Crear track | 201 |
| GET | `/tracks` | Listar todos los tracks | 200 |
| GET | `/tracks/{id}` | Obtener track por ID | 200 |
| PUT | `/tracks/{id}` | Actualizar track | 200 |
| DELETE | `/tracks/{id}` | Eliminar track | 200 |

## Ejemplos de Uso

### Crear un Artista

```json
POST http://localhost:3000/api/artistas
Content-Type: application/json

{
    "name": "Enjambre",
    "genre": "Rock"
}
```

### Crear un Álbum

```json
POST http://localhost:3000/api/albumes
Content-Type: application/json

{
    "title": "Huespedes del orbe",
    "releaseYear": 2012,
    "artistId": "UUID-DEL-ARTISTA"
}
```

### Crear un Track

```json
POST http://localhost:3000/api/tracks
Content-Type: application/json

{
    "title": "Come Together",
    "duration": 259,
    "albumId": "UUID-DEL-ALBUM"
}
```

## Pruebas con Postman

El proyecto incluye `collection postman.json` para evaluacion.

La colección incluye:
- Creación de artista con captura de ID
- Creación de álbum usando el ID del artista
- Creación de track usando el ID del álbum
- Lectura del artista con relaciones

## Integridad de Datos

1. No se puede crear un álbum sin un artista válido
2. No se puede crear un track sin un álbum válido
3. No se puede eliminar un artista si tiene álbumes asociados
4. No se puede eliminar un álbum si tiene tracks asociados
5. Los tracks se pueden eliminar sin restricciones

## Solución de Problemas

### Error: "Connection refused"
- Verificar que PostgreSQL esté ejecutándose
- Verificar credenciales en `DatabaseFactory.kt`

## Autor

Luis-Nafate


