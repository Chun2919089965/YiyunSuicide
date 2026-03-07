# YiyunSuicide


**逸云庄工作室 - 轻量级玩家自杀插件**

一个全版本兼容的 Minecraft 玩家自杀插件，支持 Spigot/Paper 1.8.8 - 1.21+


---

## 📖 简介

YiyunSuicide 是一个极其轻量级的 Minecraft 玩家自杀插件，允许玩家通过 `/killme` 命令结束自己的生命。插件设计简洁高效，支持高度自定义配置，包括世界限制、冷却时间、自定义消息等。

## ✨ 特性

- ⚡ **极致轻量** - 启动无任何日志输出，完全静默
- 🎮 **简单易用** - 仅需 `/killme` 一个命令
- 🔒 **安全可靠** - 玩家只能对自己使用，无法伤害他人
- ⏱️ **冷却系统** - 可配置冷却时间，防止滥用
- 🌍 **世界限制** - 支持黑名单/白名单机制（黑名单优先级更高）
- 💬 **自定义消息** - 所有提示消息均可通过配置文件自定义
- 🎨 **颜色支持** - 支持 `§` 和 `&` 颜色代码
- 🔐 **权限控制** - 多级权限支持，灵活管理
- 🔄 **热重载** - 支持配置文件热重载，无需重启服务器
- 📊 **数据统计** - 集成 bStats 统计，可选启用（默认开启）

## 📥 安装

### 前置要求

- **Java**: JDK 8 或更高版本
- **服务端**: Spigot 1.8.8+ / Paper 1.8.8+ 或其他兼容的 Bukkit 服务端
- **插件依赖**: 无

### 安装步骤

1. 下载最新版本的 `YiyunSuicide-1.0.0.jar`
2. 将 JAR 文件放入服务器的 `plugins` 文件夹
3. 重启服务器
4. 插件将自动生成配置文件 `plugins/YiyunSuicide/config.yml`

### 命令

| 命令 | 权限 | 说明 | 别名 |
|------|------|------|------|
| `/killme` | `yiyunsuicide.killme` | 玩家自杀 | `/suicide` |
| `/killme reload` | `yiyunsuicide.reload` | 热重载配置 | `/suicide reload` |

## ⚙️ 配置

配置文件位于 `plugins/YiyunSuicide/config.yml`

```yaml
# YiyunSuicide 配置文件
# 作者：CHL_chun
# 归属：逸云庄工作室

# 世界黑名单（优先级高于白名单）
# 在黑名单中的世界无法使用自杀功能
enable-blacklist: true
world-blacklist:
  - "world_nether"
  - "world_the_end"
  - "creative_world"

# 世界白名单
# 启用后，只有在白名单中的世界才能使用自杀功能
enable-whitelist: false
world-whitelist:
  - "world"
  - "survival_world"

# 冷却设置
enable-cooldown: true
cooldown-seconds: 10

# 消息设置
# 可用占位符：{player} - 玩家名称，{seconds} - 冷却秒数
# 支持 § 和 & 颜色代码
broadcast-message: "§c§l☠ §7 玩家 §e§l{player} §7 选择了结束自己的生命 §c§l☠"
world-denied-message: "§c 此世界不允许使用自杀功能！"
cooldown-message: "§c 自杀冷却中，请等待 §e{seconds} §c 秒"

# bStats 统计设置
# 启用后会自动收集匿名统计数据，帮助改进插件
enable-metrics: true
```

### 配置说明

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `enable-blacklist` | 是否启用世界黑名单 | `true` |
| `world-blacklist` | 黑名单世界列表 | `["world_nether", "world_the_end", "creative_world"]` |
| `enable-whitelist` | 是否启用世界白名单 | `false` |
| `world-whitelist` | 白名单世界列表 | `["world", "survival_world"]` |
| `enable-cooldown` | 是否启用冷却时间 | `true` |
| `cooldown-seconds` | 冷却时间（秒） | `10` |
| `broadcast-message` | 自杀广播消息 | - |
| `world-denied-message` | 世界限制提示消息 | - |
| `cooldown-message` | 冷却提示消息 | - |
| `enable-metrics` | 是否启用 bStats 统计 | `true` |

### 消息占位符

- `{player}` - 玩家名称（用于广播消息）
- `{seconds}` - 冷却剩余秒数（用于冷却消息）

## 🎮 命令

| 命令 | 权限 | 说明 |
|------|------|------|
| `/killme` | `yiyunsuicide.killme` | 玩家自杀 |

## 🔐 权限节点

| 权限 | 说明 | 默认值 |
|------|------|--------|
| `yiyunsuicide.killme` | 允许使用自杀命令 | `true`（所有玩家） |
| `yiyunsuicide.bypass` | 绕过冷却时间 | `op` |
| `yiyunsuicide.bypass.world` | 绕过世界限制 | `op` |
| `yiyunsuicide.reload` | 重新加载配置文件 | `op` |

## 📋 使用示例

### 基础使用

玩家在游戏中输入：
```
/killme
```

### 自定义消息

在配置文件中设置：
```yaml
broadcast-message: "&8[&4☠&8] &7{player} &c选择了结束自己的生命"
```

游戏中效果：
```
[☠] 玩家 Steve 选择了结束自己的生命
```

### 设置冷却时间

在配置文件中设置：
```yaml
enable-cooldown: true
cooldown-seconds: 30
```

玩家使用后 30 秒内再次使用会提示：
```
自杀冷却中，请等待 25 秒
```

### 配置世界黑名单

在配置文件中设置：
```yaml
enable-blacklist: true
world-blacklist:
  - "world_nether"
  - "world_the_end"
```

玩家在下界或末地使用时会提示：
```
此世界不允许使用自杀功能！
```

### 启用/禁用 bStats 统计

在配置文件中设置：
```yaml
# 启用统计（默认）
enable-metrics: true

# 禁用统计
enable-metrics: false
```

修改后需要重启服务器才能生效。

### 热重载配置文件

修改配置文件后，无需重启服务器，执行以下命令即可：

```bash
# 方式 1：使用主命令
/killme reload

# 方式 2：使用别名
/suicide reload
```

**成功提示**：
```
✓ 配置文件已重新加载！
```

**注意**：只有拥有 `yiyunsuicide.reload` 权限的玩家（默认 OP）才能执行重载命令。

## 📁 项目结构

```
YiyunSuicide/
├── src/main/
│   ├── java/com/github/playersuicide/
│   │   ├── SuicidePlugin.java      # 插件主类
│   │   ├── SuicideCommand.java     # 命令处理器
│   │   └── ConfigManager.java      # 配置管理器
│   └── resources/
│       ├── plugin.yml              # 插件描述文件
│       └── config.yml              # 默认配置文件
├── pom.xml                         # Maven 配置文件
└── README.md                       # 说明文档
```


## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 👥 作者

- **CHL_chun**

## 🏢 归属

**逸云庄工作室**

## 🙏 致谢

感谢所有使用和支持本项目的玩家！

---

