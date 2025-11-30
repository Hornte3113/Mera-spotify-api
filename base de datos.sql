-- 1. Habilitar la extensión para generar UUIDs (si usan PG < 13)
-- En versiones modernas (PG 13+) gen_random_uuid() ya viene nativo, 
-- pero esto asegura compatibilidad.
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. Tabla ARTISTAS (Padre)
CREATE TABLE artistas (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    genre VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. Tabla ALBUMES (Hija de Artistas)
CREATE TABLE albumes (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    release_year INTEGER CHECK (release_year >= 1900), -- Validación básica de año
    artist_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Restricción de Llave Foránea
    CONSTRAINT fk_artista
        FOREIGN KEY (artist_id) 
        REFERENCES artistas(id)
        ON DELETE CASCADE -- Si borro al artista, se borran sus álbumes
);

-- 4. Tabla TRACKS (Hija de Albumes)
CREATE TABLE tracks (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    duration INTEGER NOT NULL CHECK (duration > 0), -- Duración en segundos
    album_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Restricción de Llave Foránea
    CONSTRAINT fk_album
        FOREIGN KEY (album_id) 
        REFERENCES albumes(id)
        ON DELETE CASCADE -- Si borro el álbum, se borran los tracks
);

-- 5. Índices para optimizar las búsquedas por relación (Buenas prácticas)
CREATE INDEX idx_albumes_artist_id ON albumes(artist_id);
CREATE INDEX idx_tracks_album_id ON tracks(album_id);