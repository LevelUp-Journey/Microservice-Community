package com.levelup.journey.platform.moderation.domain.model.valueobjects;

public enum ReportCategory {
    HARASSMENT,          // Acoso o intimidación
    HATE_SPEECH,         // Discurso de odio  
    SPAM,                // Contenido spam
    INAPPROPRIATE,       // Contenido inapropiado
    VIOLENCE,            // Contenido violento
    RACISM,              // Contenido racista
    DISCRIMINATION,      // Discriminación
    MISINFORMATION,      // Desinformación
    COPYRIGHT,           // Violación de derechos de autor
    SEXUAL_CONTENT,      // Contenido sexual inapropiado
    SELF_HARM,           // Contenido de autolesión
    OTHER                // Otros motivos
}