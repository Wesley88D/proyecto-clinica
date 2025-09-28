# Proyecto Clinica - JPA/Hibernate (Consola)

**Autor:** Wesley Aroldo López Ortiz  
**Carné:** 2400180

## Resumen
Proyecto de consola para gestionar Pacientes, Médicos, Citas e Historial Médico con JPA/Hibernate. Base de datos H2 (archivo local `./data/clinicdb`).

## Abrir y ejecutar en IntelliJ
1. Descargar y descomprimir el zip entregado.
2. En IntelliJ: File → Open... → seleccionar la carpeta `proyecto-clinica` (donde está `pom.xml`).
3. Esperar que Maven descargue dependencias (pestaña Maven → Reimport).
4. Ejecutar la clase `com.clinica.App`:
   - Run → Edit Configurations → + → Application → Main class: `com.clinica.App`
   - O hacer clic derecho en `App.java` → Run 'App.main()'

## Notas importantes
- La base de datos H2 se guarda en `./data/clinicdb.*` (archivo creado la primera ejecución).
- Las restricciones (unique, índices, foreign keys) están definidas en las entidades JPA.
- Eliminación de paciente falla si tiene citas (decisión de integridad referencial).
