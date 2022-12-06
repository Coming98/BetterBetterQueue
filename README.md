# BetterBetterQueue

BBQ, BetterBetterQueue. An open-source cultivation software for managing daily affairs and recording growth.

# Limitation

- The interface is not distributed in proportion, so it may be ugly on other mobiles (Best support: Realme X3366)

# Dependence

- minSdk - targetSdk: 26 - 32
- [more details](app/build.gradle.template)

# Features

- Classification of TodoItem
- Statistical Analysis of TodoItem
- Export and Import TodoItem Database by json format
- TodoItem review in days

# Use-cases

## MainActivity

- [source code](app/src/main/java/com/example/betterbetterqueue/MainActivity.kt)

这是主界面，主要负责根据所选 TodoItem 的类别呈现已创建的 TodoItem 事项

<img title="" src="https://raw.githubusercontent.com/Coming98/pictures/main/202210281046766.png" alt="" data-align="center" width="106">

- [x] 顶部左侧按钮用于弹出左侧的[滑动菜单](#todocategory-drawer-layout), 提供类别选择
- [x] 顶部中间的标题支持单击后更改名称

![](https://raw.githubusercontent.com/Coming98/pictures/main/202211092040783.png)

- [x] 顶部右侧按钮用于弹出[菜单](#config-menu)提供以下功能选择:
  - [x] [星迹 - TodoItemInfoByDayActivity](#todoiteminfobydayactivity): 以天为单位查看今天所进行的 Todo 信息
  - [x] Export Database Data: 导出 JSON 格式的数据库数据 - TODO, 显示导出的位置
  - [x] Import Database Data: 导入 JSON 格式的数据库数据 - 暂时只支持从微信中选择数据库文件后以本应用打开进行导入

---

- [x] 根据当前选中的类别展示已添加的 TodoItem，每个 TodoItem 显示信息为: 名称, 创建时间, 投入时间
- [x] 内容区域支持下拉刷新
- [x] 内容区域支持右滑弹出[类别选择界面](#todocategory-drawer-layout)
- [x] 内容区域支持左滑进入[星迹](#todoiteminfobydayactivity)
- [x] 内容区域正在进行的 TodoItem 会赋予浅绿色背景标识
- [x] 内容区域今天完成过至少一次的 TodoItem 会赋予浅紫色背景标识
- [x] 内容区域今天访问并点击过开始按钮的 TodoItem 将会被默认置顶并更新置顶时间

---

- [x] 底部的悬浮按钮用于[创建新的 TodoItem](#inserttodoitemactivity)

### TodoCategory Drawer Layout

<img src="https://raw.githubusercontent.com/Coming98/pictures/main/202210281057381.png" alt="" data-align="center" width="106">


- [x] 展示所有的类别信息
  - [x] 第一个类别信息是固定名称 `星海`, 表示所有的 TodoItem 集合, 即每一个创建的 TodoItem 默认属于该类别
  - [x] 后续的类别为用户手动创建的类别集合
- [x] 单击目标类别按钮即可切换类别

### Config Menu

<img src="https://raw.githubusercontent.com/Coming98/pictures/main/202210281101279.png" title="" alt="" data-align="center">

- [x] [星迹](#todoiteminfobydayactivity): 以天为单位查看今天所进行的 Todo 信息
- [x] Export Database Data: 导出 JSON 格式的数据库数据 `Android\data\com.example.betterbetterqueue\files\Documents\*.json`
- [x] Import Database Data: 导入 JSON 格式的数据库数据 - 暂时只支持从微信中选择数据库文件后以本应用打开进行导入


<img src="https://raw.githubusercontent.com/Coming98/pictures/main/202211092027014.jpg" alt="" data-align="center" width="106">

## InsertTodoItemActivity

- [source code](app/src/main/java/com/example/betterbetterqueue/ui/TodoItem/InsertTodoItemActivity.kt)

这个界面还没太完善相关功能，主要负责 TodoItem 的新建任务

<img src="https://raw.githubusercontent.com/Coming98/pictures/main/202211092032052.jpg" alt="" data-align="center" width="106">

- [x] 插入 TodoItem 时支持选择现有类别或新建类别

## TodoItemInfoActivity

- [source code](app/src/main/java/com/example/betterbetterqueue/ui/TodoItemInfo/TodoItemInfoActivity.kt)

<img title="" src="https://raw.githubusercontent.com/Coming98/pictures/main/202211092037844.jpg" alt="" data-align="center" width="106">

- [x] 标题栏名称支持点击改名
- [x] 标题栏右侧支持置顶该 TodoItem（置顶的 TodoItem 之间按照置顶时间降序排列）

---

- [x] 中间展示的之前阶段性进展的描述内容，支持长摁编辑修改描述内容，不影响当前任务计时

![](https://raw.githubusercontent.com/Coming98/pictures/main/202211092044368.png)

---

- [x] 底部是该 TodoItem 的计时工作台，基本功能有开始计时，暂停计时，清空当前计时
  - [x] 右侧按钮支持计时时录入相关工作内容的描述或者预期计划的描述（支持本地缓存不丢失）
  - [x] 支持多任务状态缓存（多任务同时计时）

## TodoItemInfoByDayActivity

- [source code](app/src/main/java/com/example/betterbetterqueue/ui/TodoItemInfo/TodoItemInfoByDayActivity.kt)

<img src="https://raw.githubusercontent.com/Coming98/pictures/main/202210282040989.png" alt="" data-align="center" width="106">

- [x] 标题栏右侧按钮支持将时间跨度重置为今日
- [x] 支持左右滑动切换日期
- [x] 支持左滑时日期上界检测

## logic

存放业务逻辑相关的代码

- Entity: 数据模型
- dao: 数据访问接口
- model: 网络对象模型
- network: 网络数据访问接口
- Repository: UI 与 Logic 的中间层，总控数据访问接口

# Additional Documentation

- [Database - Entity](app/src/main/java/com/example/betterbetterqueue/logic/Entity/readme.md)
- [Database - Dao](app/src/main/java/com/example/betterbetterqueue/logic/Dao/readme.md)
