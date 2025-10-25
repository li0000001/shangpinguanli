# 商品保质期管家 (Expiry Tracker)

一个用于管理商品保质期的 Android 应用，能够自动计算商品到期日并同步到手机日历。

## 功能特性

- ✅ 输入商品名称、生产日期和保质期天数
- ✅ 自动计算到期日
- ✅ 自动添加到期提醒事件到手机日历
- ✅ 商品列表按到期日排序显示
- ✅ 删除商品时自动从日历中移除对应事件
- ✅ 颜色标识商品状态（已过期/即将过期/安全）

## 技术栈

- **UI 框架**: Jetpack Compose
- **架构模式**: MVVM (Model-View-ViewModel)
- **数据库**: Room
- **异步处理**: Kotlin Coroutines
- **权限管理**: Android Calendar Provider API
- **导航**: Navigation Compose

## 项目结构

```
app/src/main/java/com/expirytracker/
├── data/
│   ├── database/
│   │   ├── AppDatabase.kt       # Room 数据库
│   │   ├── Converters.kt        # 类型转换器
│   │   └── ProductDao.kt        # 数据访问对象
│   ├── model/
│   │   └── Product.kt           # 数据模型
│   └── repository/
│       └── ProductRepository.kt # 数据仓库
├── ui/
│   ├── components/
│   │   └── ProductItem.kt       # 商品列表项组件
│   ├── screens/
│   │   ├── AddProductScreen.kt  # 添加商品页面
│   │   └── ProductListScreen.kt # 商品列表页面
│   ├── theme/
│   │   ├── Color.kt             # 颜色定义
│   │   ├── Theme.kt             # 主题配置
│   │   └── Type.kt              # 字体样式
│   └── viewmodel/
│       └── ProductViewModel.kt  # ViewModel
├── utils/
│   └── CalendarUtils.kt         # 日历工具类
└── MainActivity.kt              # 主 Activity
```

## 构建说明

### 要求

- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17 或更高版本
- Android SDK 34
- Kotlin 1.9.20

### 构建步骤

1. 克隆项目
```bash
git clone <repository-url>
cd ExpiryTracker
```

2. 使用 Android Studio 打开项目

3. 同步 Gradle 依赖

4. 运行项目到模拟器或真机

## 权限说明

应用需要以下权限：

- `READ_CALENDAR`: 读取日历信息
- `WRITE_CALENDAR`: 向日历添加和删除事件

这些权限用于在用户保存商品时自动创建到期提醒事件，以及在删除商品时移除对应的日历事件。

## 使用说明

1. **添加商品**
   - 点击右下角的 "+" 按钮
   - 输入商品名称
   - 选择生产日期
   - 输入保质期天数
   - 点击"保存"

2. **查看商品列表**
   - 主界面显示所有商品
   - 按到期日排序
   - 颜色标识：
     - 🔴 红色：已过期
     - 🟠 橙色：7天内到期
     - 🟢 绿色：安全期

3. **删除商品**
   - 在商品卡片上点击垃圾桶图标
   - 商品及其对应的日历事件将被删除

## 许可证

[指定您的许可证]
