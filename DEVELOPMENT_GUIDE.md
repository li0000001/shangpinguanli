# 开发指南

## 快速开始

### 环境准备

1. 安装 [Android Studio Hedgehog (2023.1.1)](https://developer.android.com/studio) 或更高版本
2. 确保已安装 JDK 17
3. 配置 Android SDK (API Level 34)

### 项目导入

```bash
# 克隆项目
git clone <repository-url>
cd ExpiryTracker

# 使用 Android Studio 打开项目
# File -> Open -> 选择项目目录
```

### 构建项目

#### 使用 Android Studio

1. 打开项目后，Android Studio 会自动同步 Gradle
2. 等待同步完成
3. 点击 "Run" 按钮或按 Shift + F10

#### 使用命令行

```bash
# 清理项目
./gradlew clean

# 构建 Debug 版本
./gradlew assembleDebug

# 安装到设备
./gradlew installDebug

# 运行测试
./gradlew test
```

## 项目架构详解

### MVVM 架构

```
┌─────────────────────────────────────────┐
│              View (Compose UI)          │
│  - ProductListScreen                    │
│  - AddProductScreen                     │
│  - ProductItem                          │
└───────────────┬─────────────────────────┘
                │
                │ observes StateFlow
                │
┌───────────────▼─────────────────────────┐
│           ViewModel                     │
│  - ProductViewModel                     │
│  - Manages UI State                     │
│  - Handles User Actions                 │
└───────────────┬─────────────────────────┘
                │
                │ calls Repository
                │
┌───────────────▼─────────────────────────┐
│           Repository                    │
│  - ProductRepository                    │
│  - Business Logic                       │
│  - Calendar Sync                        │
└─────┬───────────────────┬───────────────┘
      │                   │
      │ DAO               │ Calendar Provider
      │                   │
┌─────▼──────┐    ┌───────▼─────────────┐
│    Room    │    │   CalendarUtils     │
│  Database  │    │  (System Calendar)  │
└────────────┘    └─────────────────────┘
```

### 数据流

1. **用户操作** → UI 组件
2. **UI 事件** → ViewModel
3. **ViewModel** → Repository
4. **Repository** → Room Database / Calendar Provider
5. **数据变化** → Flow/StateFlow
6. **StateFlow** → UI 自动更新

## 核心功能实现

### 1. 添加商品流程

```kotlin
// 用户输入 → AddProductScreen
onSaveProduct(name, productionDate, shelfLifeDays)
    ↓
// MainActivity 请求权限
if (!hasCalendarPermission()) requestCalendarPermission()
    ↓
// ViewModel 处理
viewModel.addProduct(name, productionDate, shelfLifeDays)
    ↓
// Repository 保存
repository.insertProduct(product)
    ↓
// 1. 添加到日历
CalendarUtils.addEventToCalendar()
// 2. 保存到数据库
productDao.insertProduct()
    ↓
// 3. Flow 自动通知 UI 更新
```

### 2. 删除商品流程

```kotlin
// 用户点击删除 → ProductItem
onDelete()
    ↓
// ViewModel 处理
viewModel.deleteProduct(product)
    ↓
// Repository 删除
repository.deleteProduct(product)
    ↓
// 1. 从日历删除
if (product.calendarEventId != null) {
    CalendarUtils.deleteEventFromCalendar()
}
// 2. 从数据库删除
productDao.deleteProduct()
    ↓
// 3. Flow 自动通知 UI 更新
```

### 3. 日历同步实现

```kotlin
// 添加日历事件
fun addEventToCalendar(context, productName, expiryDate): Long? {
    1. 获取默认日历 ID
    2. 创建日历事件
       - 标题: 【到期提醒】商品名称
       - 时间: 到期日当天
       - 提醒: 提前 1 小时
    3. 返回事件 ID
}

// 删除日历事件
fun deleteEventFromCalendar(context, eventId): Boolean {
    1. 根据事件 ID 删除
    2. 返回操作结果
}
```

## 开发规范

### Kotlin 代码风格

- 使用 Kotlin 官方代码风格
- 函数名使用驼峰命名法
- 常量使用大写字母和下划线
- 使用数据类 (data class) 表示数据模型
- 优先使用不可变变量 (val)

### Compose UI 规范

- 组件函数使用 @Composable 注解
- 组件名使用 PascalCase
- 状态提升到合适的层级
- 使用 remember 缓存状态
- 避免在 @Composable 函数中执行副作用

### Room 数据库规范

- Entity 类使用数据类
- DAO 使用接口
- 异步操作使用 suspend 函数
- 查询结果使用 Flow 返回

### ViewModel 规范

- 继承 ViewModel 或 AndroidViewModel
- 使用 StateFlow 暴露状态
- 使用 viewModelScope 启动协程
- 不持有 View 的引用

## 常见问题

### Q: 如何添加新的商品字段？

1. 修改 `Product.kt` 数据模型
2. 更新 Room 数据库版本号
3. 添加数据库迁移策略
4. 更新 UI 界面
5. 更新 ViewModel 逻辑

### Q: 如何自定义主题颜色？

修改 `ui/theme/Color.kt` 中的颜色定义，然后在 `Theme.kt` 中应用。

### Q: 如何处理日历权限被拒绝？

当前实现会在用户保存商品时请求权限，即使被拒绝，商品仍会保存到本地数据库，只是不会添加到日历。

### Q: 如何调试数据库内容？

使用 Android Studio 的 Database Inspector：
1. 运行应用
2. View -> Tool Windows -> App Inspection
3. 选择 Database Inspector 标签

## 测试

### 单元测试

```bash
./gradlew test
```

### UI 测试

```bash
./gradlew connectedAndroidTest
```

## 性能优化建议

1. 使用 LazyColumn 而不是 Column + Modifier.verticalScroll
2. 避免在 @Composable 函数中进行耗时操作
3. 合理使用 remember 和 derivedStateOf
4. 数据库查询使用索引
5. 图片加载使用缓存

## 调试技巧

1. 使用 Logcat 查看日志
2. 使用断点调试
3. 使用 Layout Inspector 检查 UI 布局
4. 使用 Database Inspector 检查数据库
5. 使用 Profiler 分析性能

## 发布准备

1. 修改版本号 (versionCode 和 versionName)
2. 生成签名密钥
3. 配置 ProGuard 规则
4. 构建 Release 版本
5. 测试 Release 版本
6. 上传到 Google Play 或其他应用商店
