# Próximos pasos

## 1. Dataset real verificable

- Mantener el esquema de `players.json`.
- Agregar campos opcionales: país, participaciones internacionales, resultado en Worlds, imagen y compañeros históricos.
- Separar datos verificables de ratings editoriales.
- Documentar la fuente y fecha de cada registro.

## 2. Reglas configurables

La clase `GameRules` y `application.properties` ya preparan la cantidad de opciones, tamaño de grupo, vueltas, clasificados y victorias necesarias en una serie. El siguiente refactor debe aplicar todas estas propiedades dentro de `GroupStageService` y `SimulationService`.

## 3. Persistencia

- Incorporar Spring Data JPA y H2 para desarrollo.
- Crear entidades separadas de los modelos enviados a la API.
- Guardar partidas, elecciones, resultados y semillas aleatorias.
- Migrar luego a PostgreSQL sin cambiar la capa de servicio.

## 4. Torneo completo

- Simular los cuatro grupos, no solo el grupo del usuario.
- Implementar desempates explícitos.
- Hacer el sorteo de cuartos: primero contra segundo de otro grupo.
- Evitar cruces del mismo grupo en cuartos.
- Conservar un bracket reproducible.

## 5. Calidad

- Añadir pruebas de carga del JSON, draft, grupos, desempates y Bo5.
- Inyectar una fuente de aleatoriedad con semilla para pruebas deterministas.
- Añadir validación de peticiones y respuestas HTTP uniformes.

## 6. Interfaz

- Pantalla de reglas.
- Animación de tirada y rerolls limitados.
- Vista del grupo fecha por fecha.
- Bracket visual.
- Panel de sinergias y desglose del rating.
