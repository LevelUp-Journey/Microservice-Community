# 🔐 Configuración de Cassandra para Azure Cosmos DB Cassandra API

## ✅ Cambios Realizados

He actualizado completamente tu `CassandraConfiguration.java` para **soportar Azure Cosmos DB Cassandra API** con:

### Cambios implementados:

1. ✅ **Autenticación completa**:
   - `username` y `password` se leen desde `application.yml`
   - Credenciales configuradas en el driver de Cassandra

2. ✅ **SSL/TLS habilitado**:
   - Soporte completo para conexiones SSL (obligatorio en Azure Cosmos DB)
   - SSL Context configurado para aceptar certificados de Azure
   - Flag `ssl` configurable desde variables de entorno

3. ✅ **Timeouts aumentados**:
   - `REQUEST_TIMEOUT`: 15 segundos
   - `CONNECTION_INIT_QUERY_TIMEOUT`: 15 segundos
   - `CONNECTION_CONNECT_TIMEOUT`: 15 segundos
   - `METADATA_SCHEMA_REQUEST_TIMEOUT`: 15 segundos
   - Estos timeouts son necesarios para ambientes cloud que pueden tener mayor latencia

4. ✅ **Creación automática de keyspace**:
   - El keyspace se crea automáticamente si no existe
   - Usa `SimpleStrategy` con replication factor 1 (adecuado para desarrollo/testing)

---

## 🔧 Variables de Entorno Requeridas

Tu `application.yml` está configurado para leer las credenciales desde variables de entorno. Configura las siguientes variables:

### Para Azure Cosmos DB (Cassandra API):

```bash
export CASSANDRA_CONTACT_POINTS="20.57.131.26"
export CASSANDRA_PORT="9042"
export CASSANDRA_KEYSPACE="community_keyspace"
export CASSANDRA_DATACENTER="West US 2"  # ⚠️ IMPORTANTE: Verifica el nombre exacto en Azure Portal
export CASSANDRA_USERNAME="<TU_USUARIO_AZURE>"
export CASSANDRA_PASSWORD="<TU_PASSWORD_AZURE>"
export CASSANDRA_SSL="true"  # Obligatorio para Azure Cosmos DB
```

### 📍 Cómo obtener las credenciales en Azure Portal:

1. Ve al **Azure Portal** → Tu instancia de **Cosmos DB**
2. En el menú lateral, selecciona **"Connection String"** o **"Keys"**
3. Copia los siguientes valores:
   - **CONTACT_POINTS**: Hostname del endpoint (ej: `your-account.cassandra.cosmos.azure.com`)
   - **PORT**: Generalmente `10350` para Cosmos DB (no 9042)
   - **USERNAME**: Nombre de tu cuenta Cosmos DB
   - **PASSWORD**: "Primary Password" o "Primary Key"
   - **DATACENTER**: Verifica en "Replicate data globally" (ej: "West US 2", "East US")

> ⚠️ **NOTA IMPORTANTE**: Azure Cosmos DB Cassandra API generalmente usa el puerto **10350** (no 9042). Verifica en tu connection string.

---

## 🔍 Verificar el Datacenter Correcto

El nombre del datacenter **DEBE coincidir exactamente** con el configurado en Azure. Para verificarlo:

### Opción 1: En Azure Portal
1. Ve a tu cuenta de Cosmos DB
2. Menú lateral → "Replicate data globally"
3. Verás regiones como "West US 2", "East US", etc.

### Opción 2: Con cqlsh (si tienes instalado)
```bash
cqlsh <tu-host> 10350 -u <usuario> -p <password> --ssl

# Dentro de cqlsh:
SELECT data_center FROM system.local;
```

El resultado debe ser exactamente el mismo que pones en `CASSANDRA_DATACENTER`.

---

## 🧪 Probar la Conexión

### Opción 1: Ejecutar la aplicación

```bash
./mvnw spring-boot:run
```

**Deberías ver en los logs**:
```
SSL enabled for Cassandra connection (datacenter: West US 2)
Connected to Cassandra at 20.57.131.26:9042
Keyspace 'community_keyspace' created or already exists
```

### Opción 2: Probar manualmente con cqlsh

```bash
cqlsh 20.57.131.26 10350 -u <TU_USUARIO> -p <TU_PASSWORD> --ssl
```

Si conecta exitosamente, verás el prompt `cqlsh>`.

### Opción 3: Verificar el puerto con openssl

```bash
openssl s_client -connect 20.57.131.26:9042
```

Si ves un certificado SSL válido, significa que el puerto está configurado correctamente para SSL.

---

## 🚨 Solución de Problemas

### ❌ Error: "not an SSL/TLS record"
```
io.netty.handler.ssl.NotSslRecordException: not an SSL/TLS record
```

**Causa**: Intentas conectar sin SSL a un servidor que requiere SSL.

**Solución**: 
- ✅ Verifica que `CASSANDRA_SSL=true` esté configurado
- ✅ Asegúrate de usar el puerto correcto (generalmente 10350 para Cosmos DB)

---

### ❌ Error: "Query timed out after PT2S"
```
CassandraDriverTimeoutException: Query timed out after PT2S
```

**Causa**: Los timeouts predeterminados (2 segundos) son insuficientes para Cosmos DB.

**Solución**: 
- ✅ **YA RESUELTO** - Ahora usamos timeouts de 15 segundos
- Si aún persiste, aumenta los timeouts en `getSessionBuilderConfigurer()`

---

### ❌ Error: "Node requires authentication"
```
Authentication error: Node requires authentication
```

**Causa**: Falta configurar username/password.

**Solución**: 
- ✅ **YA RESUELTO** - La configuración ahora incluye autenticación
- Verifica que las variables de entorno estén configuradas correctamente

---

### ❌ Error: "All host(s) tried for query failed"
```
All host(s) tried for query failed (no host was tried)
```

**Posibles causas**:
1. **Datacenter incorrecto**: El `CASSANDRA_DATACENTER` no coincide con el de Azure
2. **Firewall**: Tu IP no está en la whitelist de Azure
3. **Puerto incorrecto**: Estás usando 9042 en lugar de 10350

**Solución**:
1. Verifica el datacenter exacto en Azure Portal
2. Agrega tu IP pública a las reglas de firewall de Cosmos DB:
   - Azure Portal → Tu Cosmos DB → "Firewall and virtual networks"
   - Agrega tu IP pública actual
3. Verifica el puerto en el connection string de Azure

---

## 📝 Detalles Técnicos de la Implementación

### SSL Context Personalizado

La configuración incluye un `SSLContext` personalizado que acepta todos los certificados:

```java
private SSLContext createSSLContext() throws Exception {
    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(null, new javax.net.ssl.TrustManager[] {
        new javax.net.ssl.X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[0];
            }
            public void checkClientTrusted(...) {}
            public void checkServerTrusted(...) {}
        }
    }, new java.security.SecureRandom());
    return sslContext;
}
```

> ⚠️ **Nota de Seguridad**: Este TrustManager acepta todos los certificados. Para producción, considera usar un TrustStore con los certificados de Azure.

### Timeouts Configurados

Los timeouts se configuran a través del `DriverConfigLoader`:

```java
DriverConfigLoader configLoader = DriverConfigLoader.programmaticBuilder()
    .withDuration(DefaultDriverOption.REQUEST_TIMEOUT, Duration.ofSeconds(15))
    .withDuration(DefaultDriverOption.CONNECTION_INIT_QUERY_TIMEOUT, Duration.ofSeconds(15))
    .withDuration(DefaultDriverOption.CONNECTION_CONNECT_TIMEOUT, Duration.ofSeconds(15))
    .withDuration(DefaultDriverOption.METADATA_SCHEMA_REQUEST_TIMEOUT, Duration.ofSeconds(15))
    .build();
```

Estos valores son adecuados para Azure Cosmos DB, que puede tener mayor latencia que una instancia local de Cassandra.

---

## 🎯 Checklist de Configuración

Antes de ejecutar tu aplicación, asegúrate de que:

- [ ] Las variables de entorno están configuradas correctamente
- [ ] `CASSANDRA_SSL=true` está configurado
- [ ] El `CASSANDRA_DATACENTER` coincide exactamente con el de Azure
- [ ] El `CASSANDRA_PORT` es el correcto (probablemente 10350)
- [ ] Tu IP está en la whitelist del firewall de Azure Cosmos DB
- [ ] Las credenciales (username/password) son correctas
- [ ] El proyecto compila sin errores: `./mvnw clean compile`

---

## 🚀 Próximos Pasos

1. ✅ Configura las variables de entorno con tus credenciales reales
2. ✅ Verifica el datacenter y puerto correctos en Azure Portal
3. ✅ Ejecuta la aplicación: `./mvnw spring-boot:run`
4. ✅ Verifica los logs para confirmar la conexión exitosa
5. ✅ Prueba crear datos en Cassandra

---

## 📊 Diferencias: Cassandra Local vs Azure Cosmos DB

| Característica | Cassandra Local | Azure Cosmos DB Cassandra API |
|---|---|---|
| **Puerto** | 9042 | 10350 (generalmente) |
| **SSL** | Opcional | **Obligatorio** |
| **Autenticación** | Opcional | **Obligatoria** |
| **Timeouts** | 2s (predeterminado) | 15s+ (recomendado) |
| **Datacenter** | `datacenter1` | Nombre de región Azure |
| **Replication** | Configurable | Gestionado por Azure |

---

## 💡 Notas Finales

1. **Seguridad**: Nunca commits credenciales en el código. Usa variables de entorno o Azure Key Vault.

2. **Replication Strategy**: 
   - Actual: `SimpleStrategy` con RF=1 (desarrollo/testing)
   - Producción: Considera `NetworkTopologyStrategy` con múltiples datacenters

3. **Schema Management**: 
   - Actual: `CREATE_IF_NOT_EXISTS` - crea tablas automáticamente
   - Producción: Usa `NONE` y gestiona el schema con migraciones

4. **Monitoreo**: 
   - Habilita métricas de Spring Actuator
   - Monitorea las métricas de Cassandra en Azure Portal

---

¿Necesitas ayuda adicional con la configuración? 🚀

