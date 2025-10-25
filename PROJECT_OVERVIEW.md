# 商品保质期管家 - 项目概览

## 项目说明

这是一个使用现代 Android 开发技术构建的商品保质期管理应用。应用采用 MVVM 架构模式，使用 Jetpack Compose 构建 UI，Room 作为本地数据库，并集成了 Android Calendar Provider API 实现日历同步功能。

## 核心功能实现

### 1. 数据层 (Data Layer)

#### Product.kt - 商品数据模型
- 使用 Room Entity 注解
- 包含商品基本信息：名称、生产日期、保质期、到期日
- 存储对应的日历事件 ID
- 提供辅助方法：判断是否过期、计算剩余天数

#### ProductDao.kt - 数据访问对象
- 查询所有商品（按到期日排序）
- 插入、更新、删除商品操作
- 使用 Flow 实现响应式数据流

#### AppDatabase.kt - Room 数据库
- 使用单例模式
- 包含类型转换器支持 LocalDate

#### Converters.kt - 类型转换器
- LocalDate 与 String 的相互转换

#### ProductRepository.kt - 数据仓库
- 封装数据访问逻辑
- 整合日历同步功能
- 在添加/删除商品时自动同步日历事件

### 2. UI 层 (UI Layer)

#### MainActivity.kt
- 应用入口
- 处理日历权限请求
- 管理 Compose 导航

#### ProductListScreen.kt - 商品列表页面
- 显示所有商品，按到期日排序
- 空状态提示
- FAB 按钮添加新商品
- 支持删除商品

#### AddProductScreen.kt - 添加商品页面
- 输入商品名称
- 选择生产日期（支持日期选择器）
- 输入保质期天数
- 表单验证
- 自动计算到期日

#### ProductItem.kt - 商品列表项组件
- 显示商品详细信息
- 颜色状态指示（红色=已过期，橙色=7天内到期，绿色=安全）
- 删除按钮

#### Theme - Material Design 3 主题
- Color.kt - 颜色定义
- Type.kt - 字体样式
- Theme.kt - 主题配置

### 3. ViewModel 层

#### ProductViewModel.kt
- 继承 AndroidViewModel
- 管理商品列表状态
- 提供添加、更新、删除商品的方法
- 使用 StateFlow 暴露数据

### 4. 工具类

#### CalendarUtils.kt
- 添加事件到日历（带提醒）
- 从日历删除事件
- 获取默认日历 ID
- 处理权限异常

## 技术亮点

1. **现代 Android 架构**
   - MVVM 架构模式
   - 单向数据流
   - 响应式编程

2. **Jetpack Compose UI**
   - 声明式 UI
   - Material Design 3
   - 组件化开发

3. **Room 数据库**
   - 类型安全
   - 编译时查询验证
   - Flow 支持

4. **Kotlin Coroutines**
   - 异步操作
   - 结构化并发
   - 简洁的异步代码

5. **Calendar Provider 集成**
   - 自动创建日历事件
   - 同步删除
   - 带提醒功能

## 权限说明

应用需要以下运行时权限：

- `READ_CALENDAR` - 读取日历信息（获取日历 ID）
- `WRITE_CALENDAR` - 写入日历事件

权限在 MainActivity 中动态请求，用户保存商品时触发。

## 数据流

```
User Input (Compose UI)
    ↓
ProductViewModel
    ↓
ProductRepository
    ├─→ ProductDao (Room Database)
    └─→ CalendarUtils (Calendar Provider)
    ↓
StateFlow → Compose UI 更新
```

## 构建要求

- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17
- Android SDK 34
- Gradle 8.2
- Kotlin 1.9.20

## 依赖库

- androidx.core:core-ktx
- androidx.compose.ui
- androidx.compose.material3
- androidx.room
- androidx.navigation:navigation-compose
- androidx.lifecycle:lifecycle-viewmodel-compose
- kotlinx-coroutines-android

## 下一步改进建议

1. 添加商品分类功能
2. 实现商品搜索和筛选
3. 添加商品图片上传
4. 支持导入/导出数据
5. 添加统计和图表展示
6. 实现通知提醒（除日历外的应用内通知）
7. 支持多语言
8. 添加暗黑模式优化
9. 实现数据备份到云端
10. 添加条形码扫描功能
