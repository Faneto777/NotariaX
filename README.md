# NotariaX 📄

Aplicación móvil desarrollada en **Android (Kotlin)** con **Jetpack Compose** para simplificar trámites notarialmente simulados de forma rápida, segura y accesible.

## 🚀 Características Principales

- **Inicio de Sesión:** Autenticación del backend por correo y persistencia local de la sesión con **Room Database**.
- **Gestión de Trámites:** Envío al backend de solicitudes con antecedentes y estado.
- **Captura de Documentos Base:** Integración con el recurso nativo de la **Cámara** mediante permisos en tiempo de ejecución (`Manifest.permission.CAMERA`).
- **Firma Electrónica:** Registro de la firma del trámite en el backend y generación de **Notificaciones Locales**.
- **Navegación Fluida:** Manejo de estado y flujo entre pantallas con **Navigation Compose** y animaciones.
- **API externa:** Consulta de publicaciones, comentarios, álbumes, fotos, tareas y usuarios con Retrofit.
- **Backend Spring Boot:** Registro, inicio de sesión, recuperación, perfil, trámites y firmas mediante Retrofit.

## 🛠️ Arquitectura y Tecnologías

El proyecto sigue el patrón de arquitectura **MVVM (Model-View-ViewModel)** recomendado por Google, logrando desacoplar totalmente la interfaz gráfica de la lógica de negocio y persistencia.

- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose (Material3)
- **Persistencia:** Room Database
- **Asincronía:** Kotlin Coroutines & StateFlow
- **Consumo REST:** Retrofit 2 y Gson
- **Navegación:** Navigation Compose
- **Recursos Nativos:** Cámara (CameraX / Intent Image Capture) y Notificaciones del sistema (`NotificationManager`)

## 📁 Estructura del proyecto

```text
app/src/main/java/com/example/notariax/
├── data/
│   ├── local/        # Persistencia local con Room
│   └── repository/   # Acceso a los datos
├── model/            # Entidades del dominio y de Room
├── ui/
│   ├── navigation/  # Rutas y navegación entre pantallas
│   └── theme/       # Colores y tema visual
├── view/             # Pantallas Jetpack Compose
├── viewmodel/        # Estado y acciones de cada pantalla
└── MainActivity.kt   # Punto de entrada de la aplicación
```

## 📋 Requisitos e Instalación

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/Faneto777/NotariaX.git
   ```
2. Abrir el proyecto en **Android Studio (Ladybug o posterior)**.
3. Sincronizar el proyecto con Gradle (`Sync Project with Gradle Files`).
4. Ejecutar en un emulador Android (API 24+) o dispositivo físico.

## 🧪 Evaluación 4: API externa y pruebas

La pantalla Home consulta [JSONPlaceholder](https://jsonplaceholder.typicode.com/), una API pública de datos de prueba. La solicitud `POST /posts` es demostrativa: la API devuelve una respuesta, pero no conserva el registro.

Rutas consumidas desde `ExternalApiService`:

| Método | Ruta |
| --- | --- |
| GET | `/posts` |
| GET | `/posts/{id}` |
| GET | `/comments` |
| GET | `/albums` |
| GET | `/photos` |
| GET | `/todos` |
| GET | `/users` |
| POST | `/posts` |

Las pruebas unitarias de `LoginViewModel` y `RegisterViewModel` usan JUnit, MockK y un dispatcher de coroutines de prueba:

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

Para generar el reporte JaCoCo de esos dos ViewModels y verificar que cada paquete mantenga al menos 80% de cobertura de líneas, ejecuta:

```powershell
.\gradlew.bat :app:jacocoViewModelCoverageReport
```

El reporte HTML queda en `app/build/reports/jacoco/jacocoViewModelCoverageReport/html/index.html`. La tarea falla si Login o Registro baja del 80%.

## 🔌 Backend Xano

La aplicación consume Xano con Retrofit y Gson. La URL base está configurada en `BackendRetrofitClient` como `https://x8ki-letl-twmt.n7.xano.io/api:7VNgQ7Kr/`; las rutas del servicio son relativas a esa URL. El manifest conserva el permiso de Internet y no necesita habilitar HTTP sin cifrar.

### Rutas confirmadas

Los campos JSON usan camelCase, salvo `created_at`, y se corresponden con los DTOs de `data/remote/dto/BackendDtos.kt`.

| Método | Ruta | Body o query | Respuesta |
| --- | --- | --- | --- |
| POST | `/usuarios/registro` | `rut`, `nombreCompleto`, `email`, `password` | Usuario con `id`, `created_at`, `rut`, `nombreCompleto`, `email` y `password` |
| POST | `/usuarios/login` | `email`, `password` | Datos del usuario |
| POST | `/usuarios/recuperar` | `email` | La app revisa también el cuerpo de respuesta |
| GET | `/firmas-por-tramite` | Query `tramiteId` | Lista de firmas del trámite |

Xano puede responder HTTP `200` incluso cuando una operación falla. En ese caso puede devolver `{"payload":"RUT ya registrado","statement":"Throw Error"}`. La app revisa `statement` y muestra `payload` como error antes de guardar la sesión o indicar éxito.

El servicio Retrofit también declara las operaciones de perfil, trámites y firmas usadas por la app. La firma envía `tramiteId` y `firmado`; el PIN se valida localmente y no se envía al servicio.

## 📦 APK de release firmado

El proyecto requiere JDK 17 para ejecutar Gradle. El criterio del daemon está en `gradle/gradle-daemon-jvm.properties`; si Android Studio solicita descargarlo, acepta JDK 17.

Para generar el APK firmado, ejecuta desde la carpeta del proyecto:

```powershell
.\gradlew.bat :app:assembleRelease
```

El APK queda en `app/build/outputs/apk/release/app-release.apk`. La llave `notariax-release.jks` y el archivo local `keystore.properties` no se incluyen en Git para no publicar la llave ni sus contraseñas.

Capturas de los archivos generados localmente:

| APK firmado | Llave de firma local |
| --- | --- |
| ![APK firmado en la carpeta de release](docs/screenshots/apk-release.png) | ![Archivo JKS local en la carpeta del proyecto](docs/screenshots/signing-key.png) |

---
*Proyecto educativo desarrollado para la asignatura DSY1105.*
