# RSUiKit 🎨

**RSUiKit**, Android projeleri için geliştirilmiş, **yeniden kullanılabilir**, **tema uyumlu (Light / Dark)**, **XML tabanlı** bir UI Kit kütüphanesidir.  
Amaç; buton, input, text, card gibi bileşenleri tek bir merkezden yönetip, farklı projelerde **aynı tasarım diliyle** hızlıca kullanabilmektir.

---

## ✨ Özellikler

- ✅ Android **Library (AAR)** olarak dağıtım
- ✅ **JitPack** üzerinden kolay entegrasyon
- ✅ XML + Java (Compose yok)
- ✅ Light / Dark theme desteği
- ✅ Custom View yapısı (`RSButton` vb.)
- ✅ Design Token yaklaşımı (renk, radius, spacing)
- ✅ Demo `app` modülü ile canlı test

---

## 📦 Modüller

| Modül | Açıklama |
|------|---------|
| `uikit` | Asıl UI Kit kütüphanesi |
| `app` | Demo / test uygulaması |

---

## 🚀 Kurulum (JitPack)

### 1️⃣  Repository ekle

**`settings.gradle`**
```gradle
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url "https://jitpack.io" }
    }
}
```

### 2️⃣  Dependency ekle

**`build.gradle`**
```gradle
dependencies {
    implementation "com.github.RealSolutionsEmbeddedTeam:RSUiKit:1.0.0"
}
```

### 🧩 Kullanım Örneği – RSButton

```xml
<com.realsolutions.uikit.RSButton
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="Primary Button"
    app:rsType="primary"/>

```

#### Desteklenen Tipler

- primary
- secondary
- neutral
- plain_dark
- plain_light
