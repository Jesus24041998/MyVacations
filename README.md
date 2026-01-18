Registro de Vacaciones — POC Compose

Prueba de conceptos funcionales para una aplicación móvil Android que permite gestionar solicitudes de vacaciones del personal. Implementada con Jetpack Compose, arquitectura moderna y navegación declarativa.

OBJETIVO
Validar la viabilidad funcional y de interfaz de:
- Creación y edición de solicitudes de vacaciones
- Visualización de estado de aprobación
- Cálculo de días consumidos vs disponibles
- Persistencia local temporal
- Flujo de navegación entre pantallas
- Diseño UI/UX basado en Material 3

No cubre integración con backend real ni autenticación; los datos son simulados para fines de demostración.

CARACTERÍSTICAS
- Pantalla de inicio con listado de vacaciones
- Formulario de nueva solicitud
- Vista de detalle con estatus
- Notificaciones locales simuladas
- Theming dinámico (claro/oscuro)
- Estado administrado con ViewModel + StateFlow

TECNOLOGÍAS EMPLEADAS
- Kotlin 2.0.10
- Jetpack Compose (Material 3 + Navigation)
- Firebase Auth (Google Sign-In)
- Hilt para DI
- Architecture Components (ViewModel, StateFlow)
- Room o repositorio in-memory
- Play Services Auth
- Libphonenumber para validación de teléfono

CATÁLOGO DE VERSIONES (GRADLE VERSION CATALOG)
El proyecto utiliza libs.versions.toml para centralizar versiones de dependencias, librerías y plugins.

BOMs utilizados:
- Firebase BOM 33.1.2
- Compose BOM 2024.06.00

Librerías principales:
- Compose Material / Material3
- Navigation Compose
- Firebase Analytics / Auth / UI Auth
- Play Services Auth
- Credential Manager
- Libphonenumber
- Hilt (android, compiler, testing)

Plugins declarados:
- Android Application (AGP 8.5.2)
- Kotlin Android
- Compose Compiler Plugin
- Hilt Gradle Plugin
- Google Services Plugin
- KSP

Ejemplo de uso en build.gradle.kts (texto descriptivo)
plugins: android.application, kotlin.android, compose.compiler, hilt.gradle, ksp, googleservices
dependencies: platform compose bom, platform firebase bom, activity compose, material3, navigation compose, play services auth, firebase auth ktx, firebase analytics, firebase ui auth, hilt android, ksp hilt compiler

FLUJO FUNCIONAL
1. Usuario accede a la lista de solicitudes
2. Puede crear una nueva solicitud seleccionando fechas
3. Sistema calcula días y valida disponibilidad
4. Solicitud queda marcada como Pendiente
5. Simulación de respuesta que cambia a Aprobado/Rechazado

INSTALACIÓN Y EJECUCIÓN
Requisitos:
- Android Studio Flamingo o superior
- SDK 24+
- Kotlin 2.0.10+

Pasos:
1. Clonar el repositorio
2. Abrir en Android Studio
3. Sincronizar Gradle
4. Ejecutar en dispositivo o emulador

ESTADO DEL PROYECTO
Alfa técnico para evaluación interna.

TAREAS PENDIENTES
- Integración con API real
- Autenticación y perfiles con backend
- Permisos de calendario corporativo
- Tests unitarios e instrumentados
- Internacionalización
- Accesibilidad avanzada

LICENCIA
Por definir.
