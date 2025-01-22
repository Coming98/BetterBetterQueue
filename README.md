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

This is the main interface, primarily responsible for displaying the created TodoItems based on the category of the selected TodoItem.

<img title="" src="https://raw.githubusercontent.com/Coming98/pictures/main/202210281046766.png" alt="" data-align="center" width="106">

- [x] The button on the top left is used to open the [sliding menu](#todocategory-drawer-layout) on the left, allowing users to select a category.
- [x] The title in the top center supports renaming by clicking on it.

![](https://raw.githubusercontent.com/Coming98/pictures/main/202211092040783.png)

- [x] The button on the top right is used to open the [menu]((#config-menu), providing the following options:
  - [x] [Star Trail - TodoItemInfoByDayActivity](#todoiteminfobydayactivity): View the details of today's Todo items on a daily basis.
  - [x] Export Database Data: Export the database data in JSON format and display the export location.
  - [x] Import Database Data: Import database data in JSON format – currently only supports selecting a database file from WeChat and opening it with this application for import.
---

- [x] Display the added TodoItems based on the currently selected category. Each TodoItem shows the following information: name, creation time, and invested time.
- [x] The content area supports pull-to-refresh functionality.
- [x] The content area supports a right swipe to open the [category selection interface](#todocategory-drawer-layout).
- [x] The content area supports a left swipe to navigate to [Star Trail](#todoiteminfobydayactivity).
- [x] TodoItems in progress within the content area are highlighted with a light green background.
- [x] TodoItems in the content area that have been completed at least once today are highlighted with a light purple background.
- [x] TodoItems in the content area that have been accessed and had their start button clicked today will be automatically pinned to the top, with their pin time updated.

---

- [x] The floating button at the bottom is used to [create a new TodoItem](#inserttodoitemactivity).

### TodoCategory Drawer Layout

<img src="https://raw.githubusercontent.com/Coming98/pictures/main/202210281057381.png" alt="" data-align="center" width="106">


- [x] Display all category information.
  - [x] The first category is fixed with the name `Star Ocean`, representing the collection of all TodoItems. Every TodoItem created is assigned to this category by default.
  - [x] Subsequent categories are user-created collections.
- [x] Clicking on the target category button allows you to switch categories.

### Config Menu

<img src="https://raw.githubusercontent.com/Coming98/pictures/main/202210281101279.png" title="" alt="" data-align="center">

- [x] [Star Trail](#todoiteminfobydayactivity): View today's Todo details on a daily basis.
- [x] Export Database Data: Export database data in JSON format. `Android\data\com.example.betterbetterqueue\files\Documents\*.json`
- [x] Import Database Data: Import database data in JSON format – currently only supports selecting a database file from WeChat and opening it with this application for import.


<img src="https://raw.githubusercontent.com/Coming98/pictures/main/202211092027014.jpg" alt="" data-align="center" width="106">

## InsertTodoItemActivity

- [source code](app/src/main/java/com/example/betterbetterqueue/ui/TodoItem/InsertTodoItemActivity.kt)

- [X] Primarily responsible for creating new TodoItem tasks.

<img src="https://raw.githubusercontent.com/Coming98/pictures/main/202211092032052.jpg" alt="" data-align="center" width="106">

- [x] When inserting a TodoItem, users can choose an existing category or create a new one.

## TodoItemInfoActivity

- [source code](app/src/main/java/com/example/betterbetterqueue/ui/TodoItemInfo/TodoItemInfoActivity.kt)

<img title="" src="https://raw.githubusercontent.com/Coming98/pictures/main/202211092037844.jpg" alt="" data-align="center" width="106">

- [x] The title bar name supports renaming by clicking on it.
- [x] The right side of the title bar supports pinning the TodoItem (pinned TodoItems are sorted in descending order based on their pin time).

---

- [x] The description content of previous progress displayed in the middle supports long-press editing to modify the description without affecting the current task timer.

![](https://raw.githubusercontent.com/Coming98/pictures/main/202211092044368.png)

---

- [x] At the bottom is the timer workspace for the TodoItem, with basic functions including starting the timer, pausing the timer, and resetting the current timer.
  - [x] The right-side button allows for inputting descriptions of work done during the timer or planned tasks (with support for local caching to prevent data loss).
  - [x] Supports multi-task state caching (allowing simultaneous timing for multiple tasks).

## TodoItemInfoByDayActivity

- [source code](app/src/main/java/com/example/betterbetterqueue/ui/TodoItemInfo/TodoItemInfoByDayActivity.kt)

<img src="https://raw.githubusercontent.com/Coming98/pictures/main/202210282040989.png" alt="" data-align="center" width="106">

- [x] Supports multi-task state caching (simultaneous timing for multiple tasks).
- [x] The right-side button in the title bar supports resetting the time span to today.
- [x] Supports detecting the upper date limit when swiping left.

## logic

Stores business logic-related code:

Entity: Data models
DAO: Data Access Interfaces
Model: Network object models
Network: Network data access interfaces
Repository: The intermediary layer between UI and logic, managing overall data access interfaces

# Additional Documentation

- [Database - Entity](app/src/main/java/com/example/betterbetterqueue/logic/Entity/readme.md)
- [Database - Dao](app/src/main/java/com/example/betterbetterqueue/logic/Dao/readme.md)
