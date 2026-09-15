# World Roster Challenge

MVP de un juego de navegador para construir un roster competitivo y simular un torneo con el formato tradicional de grupos.

## Requisitos

- Java 17 o superior
- Maven 3.9 o superior

## Ejecutar

```bash
mvn spring-boot:run
```

Luego abre `http://localhost:8080`.

## Reglas implementadas

- Draft de Top, Jungle, Mid, ADC y Support.
- Tres candidatos aleatorios por posición.
- Rating individual y bonificaciones de sinergia.
- Grupo de cuatro equipos con doble vuelta.
- Seis partidas por equipo.
- Los dos primeros clasifican.
- Cuartos, semifinales y final al mejor de cinco.
- Cada partida posee un identificador independiente.

Los nombres y atributos del dataset inicial son ficticios.

## Catálogo JSON

Los jugadores se cargan desde `src/main/resources/data/players.json`. El repositorio valida al iniciar que los IDs sean únicos, que los ratings estén entre 0 y 100 y que existan al menos tres candidatos por posición.

El catálogo completo puede consultarse en `GET /api/catalog/players`. Para usar otro archivo, cambia `game.players.resource` en `application.properties`.

## Continuación del desarrollo

Consulta `docs/NEXT_STEPS.md` para el plan de dataset real, persistencia, torneo completo, pruebas e interfaz.

## Torneo con semilla

La simulación acepta una semilla opcional: `POST /api/games/{gameId}/simulate?seed=12345`. Con el mismo roster y la misma semilla se obtienen los mismos cuatro grupos, resultados y bracket. Si no se envía, el servidor genera una semilla y la devuelve en la respuesta.

La fase de grupos contiene 16 equipos distribuidos en cuatro grupos. Los cruces de cuartos son A1-B2, C1-D2, B1-A2 y D1-C2.

## Datos históricos reales

El catálogo v2 usa identidades, organizaciones, años, posiciones y logros reales de la historia competitiva. Los cuatro atributos 0-100 no son estadísticas oficiales: son estimaciones editoriales para balance del juego, claramente marcadas como `CURATED_EDITORIAL`. Las fuentes y la metodología están embebidas en el JSON. Los rivales del torneo son equipos históricos reales identificados por organización y año.
