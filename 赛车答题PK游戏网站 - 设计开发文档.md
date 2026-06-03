# 赛车答题PK游戏网站 - 设计开发文档

本文档为双人同屏PK赛车答题游戏网站的完整设计开发指南，涵盖项目全流程细节，可直接保存为.md文件，用于开发团队协作、需求落地及后期维护，确保开发过程规范、功能符合预期。本次迭代核心调整：前端采用Vue实现（摒弃原生HTML主导开发），题目池从数据库获取（取消浏览器本地存储缓存题目）。

# 一、文档说明

## 1.1 文档目的

明确赛车答题PK游戏网站的开发需求、技术选型、架构设计、功能实现及测试上线标准，为开发人员提供清晰的开发指引，为项目验收提供依据，保障项目高效、有序推进。

## 1.2 适用范围

本文档适用于参与本项目的所有开发人员、测试人员、产品人员，涵盖需求分析、设计、开发、测试、上线全流程，作为项目推进的核心参考文档。

## 1.3 版本说明

|版本号|更新日期|更新内容|更新人|
|---|---|---|---|
|V1.0|初始版本|完成文档整体框架搭建，明确核心需求、技术选型及基础实现逻辑|开发团队|
|V1.1|迭代版本|补全核心逻辑伪代码，完善测试用例及上线流程，优化文档细节|开发团队|
|V1.2|迭代版本|调整页面为四列布局，新增赛车动态视觉效果、终点距离显示，修改题目数据获取方式为后端请求|开发团队|
|V1.3|迭代版本|前端替换为Vue实现（摒弃原生HTML主导），题目池改为从数据库获取，取消浏览器本地存储题目缓存，同步调整技术选型、架构及核心逻辑|开发团队|
# 二、项目概述

## 2.1 项目背景

开发一款基于网页端的双人同屏PK赛车答题游戏，核心定位为“娱乐+知识”结合的轻量级网页游戏，适配PC端使用场景，支持两名玩家共用一台电脑，以四列分屏形式（左答题区-左赛车轨迹-右赛车轨迹-右答题区）进行答题竞速。玩家通过答题结果（答对/答错）控制各自赛车的速度，赛车轨迹区实现树木向后移动的动态效果，模拟赛车向前行驶的视觉体验，顶部实时显示双方距离终点的距离，最终先抵达赛道终点的玩家获得胜利，兼顾趣味性与互动性，适合休闲娱乐、好友PK等场景。前端采用Vue框架开发（摒弃原生HTML主导），题目池从数据库获取，确保题目数据可灵活管理、实时更新，取消浏览器本地存储题目，避免数据冗余和同步问题。

## 2.2 项目目标

- 功能目标：基于Vue实现四列布局（答题区+赛车轨迹区分离）、答题与赛车速度联动、赛车动态视觉效果（树木后移）、顶部终点距离显示、从数据库获取题目数据、游戏流程闭环（准备-竞速-结算-重玩），取消本地浏览器存储题目。

- 体验目标：界面简洁直观，操作低门槛（仅需鼠标点击），赛车移动流畅无卡顿，答题反馈及时，赛车动态效果逼真，终点距离显示清晰，保证双人PK的公平性，Vue组件化开发提升页面渲染效率和交互流畅度。

- 技术目标：基于Vue框架开发，轻量高效，通过后端接口从数据库查询获取题目，适配主流PC浏览器，页面加载速度≤3s，帧率≥30fps，动态效果流畅无卡顿，组件化设计提升可维护性。

- 可扩展目标：Vue组件化、模块化设计，支持后期扩展题目类型（判断、填空）、自定义赛道/赛车样式、增加历史对战记录、后端题目管理等功能，数据库存储题目便于批量更新和维护。

## 2.3 核心玩法简介

1.  准备阶段：两名玩家确认参与，查看游戏规则，点击“开始游戏”，前端通过Vue发起请求，后端从数据库查询题目池并返回，前端初始化游戏场景及Vue组件；

2.  竞速阶段：页面分为四列，第一列（左）为玩家1答题区，第二列为玩家1赛车轨迹区，第三列为玩家2赛车轨迹区，第四列（右）为玩家2答题区；赛车轨迹区显示赛道及两侧树木，树木随赛车前进向后移动，模拟行驶效果；顶部实时显示双方距离终点的剩余距离；系统从数据库返回的题目池中随机出题，玩家同步或依次答题，答对则赛车加速，答错则赛车减速；

3.  结算阶段：当任意一名玩家的赛车抵达终点，游戏立即结束，弹出结算弹窗，显示获胜者、双方答题数据（答对/答错数、总用时、最终速度、剩余距离）；

4.  重玩/退出：结算后支持“重新开始”（重置所有数据，前端重新请求后端从数据库获取题目）或“退出游戏”（返回初始页面，重置Vue组件状态）。

# 三、核心需求分析

## 3.1 功能需求（核心必做）

|需求模块|具体描述|优先级|
|---|---|---|
|Vue组件化四列布局|1.  基于Vue组件化开发，页面分为四列，宽度占比合理（建议：答题区各占20%，赛车轨迹区各占30%），对称分布；2.  第一列：玩家1答题区组件（题目文本、选项按钮、答题统计）；3.  第二列：玩家1赛车轨迹区组件（赛道、赛车、两侧树木、起点/终点标识）；4.  第三列：玩家2赛车轨迹区组件（与玩家1轨迹区组件结构一致）；5.  第四列：玩家2答题区组件（与玩家1答题区组件结构一致）；6.  布局适配不同PC分辨率（≥1366×768），无拉伸、遮挡问题，组件化实现复用性。|高|
|答题系统（数据库获取题目）|1.  题目类型：默认单选（4个选项），支持后期扩展判断、填空题；2.  题目获取：前端通过Vue发起请求，后端从数据库查询获取题目池（至少50道），游戏初始化时请求，失败则提示“题目加载失败，请重试”；3.  答题规则：两名玩家同步答题（或依次答题，可在配置文件中切换），点击选项即完成答题，不可修改；4.  答题反馈：答题后立即显示“答对”“答错”提示，同步更新答题统计数据，Vue响应式更新页面；5.  取消浏览器本地存储题目，每次出题、重玩均从数据库重新获取或从后端返回的题目池抽取，确保数据实时性。|高|
|赛车速度联动|1.  初始速度：两名玩家初始速度一致（默认10px/s）；2.  速度规则：答对1题，速度增加5px/s（可配置）；答错1题，速度减少3px/s（可配置）；3.  速度限制：最大速度≤50px/s（避免无限加速），最小速度≥0px/s（避免赛车倒车）；4.  速度显示：实时显示当前赛车速度，速度变化时有视觉反馈（如数字变色、闪烁），Vue响应式更新速度数据。|高|
|赛车动态效果与轨迹|1.  赛道参数：赛道长度固定800px（可配置），赛车从0px位置出发，抵达800px即为终点；2.  移动逻辑：每100ms刷新一次赛车位置，移动距离=当前速度×时间间隔（秒），Vue响应式更新赛车位置数据，同步渲染到页面；3.  动态效果：赛道两侧添加树木元素，树木随赛车前进向后匀速移动（移动速度与赛车速度正相关），模拟赛车向前行驶的视觉体验，通过Vue结合CSS动画实现；4.  轨迹展示：赛道背景清晰，包含起点、终点标识，赛车位置与进度条同步，直观显示双方进度差距，响应式更新进度数据。|高|
|终点距离显示|1.  显示位置：页面顶部，分为左右两部分，分别对应玩家1和玩家2，通过Vue组件实现；2.  显示内容：实时计算并显示“距离终点：XXpx”（XX为赛道长度 - 赛车当前位置），Vue响应式更新距离数据；3.  视觉反馈：距离终点越近，数字颜色越醒目（如从黑色→橙色→红色），提升竞速紧张感，通过Vue动态绑定样式实现。|高|
|游戏流程控制|1.  准备阶段：显示游戏规则弹窗组件，点击“开始游戏”后，前端通过Vue发起请求，后端从数据库获取题目池，初始化双方数据（位置、速度、答题统计），开始出题；2.  竞速阶段：实时监测双方赛车位置，Vue响应式更新答题区题目、赛车轨迹、终点距离，禁止中途退出（可增加“放弃比赛”按钮）；3.  结算阶段：一方抵达终点后，立即停止双方赛车移动、树木动态效果和答题，弹出结算弹窗组件，展示对战结果；4.  重玩功能：结算后点击“重新开始”，重置所有Vue组件状态和玩家数据，前端重新请求后端从数据库获取题目，进入新一轮游戏。|高|
## 3.2 非功能需求

|需求类型|具体要求|
|---|---|
|兼容性|支持Chrome、Edge、Firefox主流PC浏览器（版本≥80），分辨率≥1366×768，无布局错乱、功能异常问题；不支持移动端（仅适配PC端），Vue框架适配主流浏览器，无兼容性隐患。|
|性能|1.  页面加载时间≤3s，后端从数据库查询题目响应时间≤1s，资源加载无阻塞；2.  赛车位置、树木动态效果更新无明显卡顿，帧率≥30fps，Vue响应式渲染确保流畅性；3.  答题反馈响应时间≤500ms，无延迟，组件化渲染提升响应速度；4.  避免频繁请求数据库，后端可对题目池进行合理缓存（非前端浏览器缓存），提升请求效率。|
|易用性|1.  操作简单，仅需鼠标点击（开始游戏、答题、重玩），无需复杂键盘操作；2.  界面提示清晰，游戏规则、答题反馈、结算结果、终点距离一目了然，Vue组件交互更流畅；3.  无学习成本，新手可快速上手。|
|可维护性|1.  Vue组件化、模块化设计，各功能模块独立（答题组件、赛车轨迹组件、弹窗组件等），便于后期修改和扩展；2.  配置文件（速度参数、赛道长度、后端接口地址）独立，可直接修改，无需改动核心代码；3.  代码注释清晰，命名规范，Vue组件复用性强，便于开发人员后期维护和迭代；4.  题目存储在数据库，便于批量更新、删除、新增，无需修改前端代码。|
|公平性|1.  两名玩家初始条件一致（初始速度、出题难度）；2.  题目从数据库获取的题目池中随机抽取，双方答题机会均等，无偏向性；3.  速度变化规则、动态效果参数统一，无差异化对待。|
|稳定性|后端从数据库查询题目失败时，有明确提示并支持重试；游戏过程中无崩溃、卡死现象，Vue组件状态管理规范，异常情况可正常重置游戏；数据库连接稳定，避免因数据库异常导致题目加载失败。|
## 3.3 可扩展需求（可选）

- 题目管理：后端增加题目管理接口和页面，支持手动新增、编辑、删除数据库中的题目，按分类管理题目池，前端可同步适配题目分类筛选功能；

- 自定义设置：支持玩家自定义赛车样式、赛道背景、速度参数（需密码验证，避免作弊），通过Vue组件动态切换样式；

- 历史记录：后端新增对战记录数据表，存储双方对战记录（获胜者、答题数据、用时），前端通过Vue请求接口获取历史战绩并展示，支持多设备同步；

- 难度分级：增加简单、中等、困难三个难度，不同难度对应数据库中不同分类的题目、不同的速度变化幅度及树木移动速度，前端通过Vue组件切换难度；

- 音效反馈：答题、加速、减速、获胜时增加对应音效，提升游戏体验，通过Vue音频组件实现；

- 后端扩展：增加用户登录接口和用户数据表，支持多设备同步对战记录，统计玩家胜率，前端Vue适配登录组件。

# 四、架构设计

## 4.1 技术栈选择（Vue为主，支持数据库获取题目，摒弃原生HTML主导、取消浏览器本地存储题目）

|技术类型|选型|说明|
|---|---|---|
|前端框架|Vue 3（Composition API）+ Vue Router（可选）|摒弃原生HTML主导开发，采用Vue 3组件化开发，Composition API便于逻辑复用和状态管理，提升开发效率和可维护性；Vue Router用于后期扩展多页面（如登录、历史记录），当前单页面可简化使用。|
|前端样式|CSS3 + Vue Template + 可选（Tailwind CSS）|结合Vue Template实现组件化布局，CSS3实现动画效果（赛车移动、树木滚动），可选Tailwind CSS提升样式开发效率，确保四列布局对称、适配性好。|
|布局技术|Flex布局 + 响应式适配（Vue动态绑定样式）|实现四列对称布局，合理分配各列宽度，通过Vue动态绑定样式适配不同PC分辨率，避免布局错乱、元素遮挡，组件化布局提升复用性。|
|动画/交互|CSS3 Transition/Animation + Vue响应式数据 + 定时器|实现赛车平滑移动、树木向后滚动的动态效果、速度变化反馈、答题提示动画，Vue响应式数据实时更新页面，确保动画流畅性和交互及时性。|
|数据交互|Axios（Vue生态常用请求库）|替代原生AJAX，用于前端Vue发起请求，后端从数据库查询题目并返回，处理请求成功/失败逻辑，确保题目数据正常加载，取消浏览器本地存储题目。|
|后端技术（支撑数据库获取题目）|可选（Node.js/Java/Python）+ 数据库（MySQL/PostgreSQL）|后端负责接收前端请求，从数据库查询题目池并返回，处理数据库连接、数据查询逻辑；数据库用于存储题目数据（题目文本、选项、正确答案等），便于管理和更新。|
|调试工具|Chrome开发者工具 + Vue DevTools|用于调试Vue组件、响应式数据、后端接口请求、页面布局、性能优化，提升开发效率，便于排查组件状态和数据流转问题。|
## 4.2 页面结构设计（Vue单页面应用，四列布局，组件化开发）

项目采用Vue单页面应用（SPA）设计，所有功能（准备、竞速、结算）在同一个Vue页面完成，通过Vue组件化开发，拆分功能模块，取消浏览器本地存储题目，题目均从数据库获取，通过Vue响应式状态控制不同场景的显示与隐藏，提升用户体验和可维护性。

```html
├── 游戏前端（Vue项目结构）
│   ├── src/
│   │   ├── main.js：入口文件，初始化Vue实例，引入Axios、全局组件、样式
│   │   ├── App.vue：根组件，包含页面整体布局（头部、核心游戏区、控制区）
│   │   ├── components/：功能组件（拆分复用，便于维护）
│   │   │   ├── Header.vue：头部组件（游戏标题、规则按钮、退出按钮、终点距离显示区）
│   │   │   ├── PlayerAnswer.vue：玩家答题组件（复用，通过props区分玩家1/2，包含答题统计、题目、选项）
│   │   │   ├── PlayerTrack.vue：玩家赛车轨迹组件（复用，通过props区分玩家1/2，包含赛道、赛车、树木、进度条）
│   │   │   ├── RuleModal.vue：规则弹窗组件
│   │   │   ├── ResultModal.vue：结算弹窗组件
│   │   │   └── ControlArea.vue：控制区组件（开始、重玩、重试按钮，游戏状态提示）
│   │   ├── api/：接口请求封装（Axios）
│   │   │   └── questionApi.js：封装题目请求接口，调用后端接口从数据库获取题目池
│   │   ├── store/：Vue状态管理（可选，Pinia/Vuex）
│   │   │   └── gameStore.js：管理游戏全局状态（玩家数据、游戏状态、题目池等）
│   │   ├── utils/：工具函数
│   │   │   ├── randomQuestion.js：从后端返回的题目池中随机抽取题目（无本地存储）
│   │   │   ├── gameLogic.js：游戏核心逻辑（速度计算、位置更新、距离计算等）
│   │   │   └── domUtils.js：少量DOM操作工具（辅助动画实现）
│   │   ├── config/：配置文件
│   │   │   └── gameConfig.js：常量定义（速度参数、赛道长度、后端接口地址、列宽占比等）
│   │   ├── assets/：资源文件
│   │   │   ├── images/：赛车、赛道、树木、按钮图标等图片
│   │   │   └── styles/：全局样式、动画样式
│   │   └── views/：页面视图（当前单页面，后期扩展可新增）
│   │       └── GameView.vue：游戏主视图，组合所有功能组件，实现四列布局
│   ├── public/：静态资源（index.html仅作为Vue挂载容器，不用于业务逻辑开发）
│   ├── package.json：项目依赖配置
│   └── vite.config.js：构建配置（Vue 3推荐使用Vite，提升构建和运行速度）
├── 后端服务（支撑数据库查询）
│   ├── 接口层：提供题目查询接口（如/api/questions），接收前端请求
│   ├── 业务逻辑层：处理题目查询、数据筛选逻辑
│   ├── 数据访问层：连接数据库，执行SQL查询，获取题目池数据
│   └── 数据库：存储题目数据（表结构：题目ID、题目文本、选项数组、正确答案索引等）
└── 资源文件（与前端assets一致，可统一管理）
```

## 4.3 核心数据模型（Vue响应式，数据库驱动，无本地存储）

通过Vue响应式数据（Composition API的ref/reactive）定义核心数据模型，新增后端接口配置、数据库请求相关逻辑，取消浏览器本地存储题目相关代码，所有题目数据从数据库获取，便于数据管理和状态更新，所有配置可通过配置文件修改，提升可维护性。

```javascript
// 1. 游戏配置模型（config/gameConfig.js，可直接修改配置）
const gameConfig = {
  trackLength: 800,          // 赛道长度（px）
  initialSpeed: 10,          // 初始速度（px/s）
  maxSpeed: 50,              // 最大速度（px/s）
  minSpeed: 0,               // 最小速度（px/s）
  accelerateStep: 5,         // 答对加速值（px/s）
  decelerateStep: 3,         // 答错减速值（px/s）
  updateInterval: 100,       // 赛车位置、树木动态更新间隔（ms）
  treeScrollRatio: 0.8,      // 树木滚动速度与赛车速度的比例（0.8倍，模拟远景效果）
  columnWidths: {            // 四列宽度占比
    answer: 20,              // 答题区占比20%
    track: 30                // 赛车轨迹区占比30%
  },
  api: {
    questionUrl: "/api/questions" // 后端接口地址，用于查询数据库中的题目池
  },
  gameStatus: "ready"        // 游戏状态：ready（准备中）、loading（题目加载中）、racing（竞速中）、ended（已结束）
};

// 2. Vue状态管理（store/gameStore.js，使用Pinia示例，响应式管理数据）
import { defineStore } from "pinia";
import { getQuestionPool } from "@/api/questionApi"; // 从接口获取数据库题目

export const useGameStore = defineStore("game", {
  state: () => ({
    // 玩家数据（响应式）
    players: [
      {
        id: 1,
        position: 0,           // 赛车当前位置（px）
        speed: gameConfig.initialSpeed,
        correctCount: 0,       // 答对题数
        wrongCount: 0,         // 答错题数
        currentQuestion: null, // 当前题目
        isAnswering: false,    // 是否正在答题
        distanceToEnd: gameConfig.trackLength // 距离终点距离
      },
      {
        id: 2,
        position: 0,
        speed: gameConfig.initialSpeed,
        correctCount: 0,
        wrongCount: 0,
        currentQuestion: null,
        isAnswering: false,
        distanceToEnd: gameConfig.trackLength
      }
    ],
    gameStatus: gameConfig.gameStatus,
    questionPool: [],         // 从数据库获取的题目池（响应式，无本地存储）
    timer: null,              // 定时器
    loading: false,           // 题目加载状态
    errorMsg: ""              // 错误提示（如题目加载失败）
  }),
  actions: {
    // 从数据库获取题目池（通过后端接口）
    async fetchQuestionPool() {
      this.loading = true;
      this.errorMsg = "";
      try {
        const res = await getQuestionPool(); // 调用接口，后端从数据库查询题目
        this.questionPool = res.data; // 存储数据库返回的题目池
        if (this.questionPool.length< 50) {
          console.warn("数据库中题目数量不足50道，建议补充");
        }
      } catch (err) {
        this.errorMsg = "题目加载失败，请重试";
        console.error("数据库题目查询失败：", err);
      } finally {
        this.loading = false;
      }
    },
    // 答对题处理
    correctAnswer(playerId) {
      const player = this.players.find(p => p.id === playerId);
      if (!player) return;
      player.correctCount++;
      player.speed = Math.min(player.speed + gameConfig.accelerateStep, gameConfig.maxSpeed);
    },
    // 答错题处理
    wrongAnswer(playerId) {
      const player = this.players.find(p => p.id === playerId);
      if (!player) return;
      player.wrongCount++;
      player.speed = Math.max(player.speed - gameConfig.decelerateStep, gameConfig.minSpeed);
    },
    // 更新赛车位置和距离终点距离（响应式更新）
    updatePosition(playerId) {
      const player = this.players.find(p => p.id === playerId);
      if (!player) return;
      const distance = player.speed * (gameConfig.updateInterval / 1000);
      player.position = Math.min(player.position + distance, gameConfig.trackLength);
      player.distanceToEnd = Math.max(gameConfig.trackLength - player.position, 0);
      return { position: player.position, distanceToEnd: player.distanceToEnd };
    },
    // 重置游戏数据
    resetGame() {
      this.players.forEach(player => {
        player.position = 0;
        player.speed = gameConfig.initialSpeed;
        player.correctCount = 0;
        player.wrongCount = 0;
        player.currentQuestion = null;
        player.isAnswering = false;
        player.distanceToEnd = gameConfig.trackLength;
      });
      this.gameStatus = "ready";
      this.timer && clearInterval(this.timer);
      this.timer = null;
    },
    // 开始游戏（先获取题目，再启动游戏）
    async startGame() {
      if (this.gameStatus !== "ready") return;
      this.gameStatus = "loading";
      await this.fetchQuestionPool(); // 从数据库获取题目
      if (this.questionPool.length === 0) {
        this.gameStatus = "ready";
        this.errorMsg = "无可用题目，请联系管理员补充数据库题目";
        return;
      }
      // 分配初始题目
      this.players.forEach(player => {
        player.currentQuestion = this.getRandomQuestion();
      });
      // 启动定时器，更新位置和树木动态
      this.gameStatus = "racing";
      this.timer = setInterval(() => {
        this.players.forEach(player => {
          this.updatePosition(player.id);
          // 检查是否抵达终点
          if (player.position >= gameConfig.trackLength) {
            this.endGame(player.id);
          }
        });
      }, gameConfig.updateInterval);
    },
    // 随机获取题目（从数据库返回的题目池中抽取，无本地存储）
    getRandomQuestion() {
      if (this.questionPool.length === 0) return null;
      const randomIndex = Math.floor(Math.random() * this.questionPool.length);
      return this.questionPool[randomIndex];
    },
    // 结束游戏
    endGame(winnerId) {
      this.gameStatus = "ended";
      this.timer && clearInterval(this.timer);
      this.timer = null;
      // 后续可添加结算逻辑，如弹出结算弹窗
    }
  }
});

// 3. 接口封装（api/questionApi.js，Axios请求后端接口，从数据库获取题目）
import axios from "axios";
import { gameConfig } from "@/config/gameConfig";

// 获取数据库中的题目池
export const getQuestionPool = () => {
  return axios.get(gameConfig.api.questionUrl, {
    timeout: 10000, // 超时时间10s，确保数据库查询完成
    params: {
      count: 50 // 请求50道题目，后端从数据库查询并返回
    }
  });
};
```

## 4.4 核心流程架构（Vue驱动，数据库获取题目，取消本地存储）

游戏整体流程分为5个阶段，基于Vue组件化和响应式状态管理，核心调整为“从数据库获取题目”，取消浏览器本地存储题目，各阶段无缝衔接，通过Vue状态切换控制流程，确保流畅性。

1. 初始化阶段：Vue项目启动，main.js初始化Vue实例，挂载App.vue根组件，加载全局配置和组件；GameView.vue组合所有功能组件（Header、PlayerAnswer、PlayerTrack等），初始化四列布局；useGameStore初始化游戏状态，设置初始游戏状态为“ready”。

2. 题目加载阶段：玩家点击“开始游戏”，ControlArea组件触发useGameStore的startGame方法，切换游戏状态为“loading”；调用getQuestionPool接口，后端从数据库查询题目池并返回；请求成功则将题目存入questionPool（响应式数据），分配初始题目；请求失败则显示errorMsg，提供重试按钮。

3. 准备阶段：题目加载成功后，启动定时器，切换游戏状态为“racing”，进入竞速阶段，Vue响应式更新页面组件状态（显示题目、初始化赛车位置、终点距离）。

4. 竞速阶段：定时器每100ms执行一次，调用updatePosition方法，响应式更新双方赛车位置、距离终点距离（同步到Header组件显示）、树木滚动位置（PlayerTrack组件实现）；玩家点击答题选项，PlayerAnswer组件触发答题逻辑，调用correctAnswer/wrongAnswer方法，更新玩家速度和答题统计，响应式更新页面；实时监测赛车位置，若有玩家抵达终点，调用endGame方法，进入结算阶段。

5. 结算阶段：停止定时器，切换游戏状态为“ended”，ResultModal组件弹出，显示获胜者及双方数据（从useGameStore中获取响应式数据）；玩家点击“重新开始”，触发resetGame方法，重置所有响应式数据，返回准备阶段；点击“退出”，重置组件状态，返回初始页面。

# 五、核心功能实现细节（Vue版，数据库获取题目）

## 5.1 Vue组件化四列布局实现

基于Vue组件化开发，通过GameView.vue组合组件，采用Flex布局实现四列布局，合理分配各列宽度，通过props传递数据，实现组件复用，核心代码如下（简化版）：

```vue
// GameView.vue（游戏主视图，四列布局）
<template>
  <div class="game-container">
    <!-- 头部组件：包含终点距离显示 -->
    <Header :players="gameStore.players" />
    <!-- 核心游戏区：四列布局 -->
    <div class="game-main">
      <!-- 第一列：玩家1答题区（复用PlayerAnswer组件） -->
      <PlayerAnswer 
        :playerId="1"
        :currentQuestion="gameStore.players[0].currentQuestion"
        :correctCount="gameStore.players[0].correctCount"
        :wrongCount="gameStore.players[0].wrongCount"
        @answer="handleAnswer(1)"
        :isRacing="gameStore.gameStatus === 'racing'"
      />
      <!-- 第二列：玩家1赛车轨迹区（复用PlayerTrack组件） -->
      <PlayerTrack 
        :playerId="1"
        :position="gameStore.players[0].position"
        :speed="gameStore.players[0].speed"
        :distanceToEnd="gameStore.players[0].distanceToEnd"
        :isRacing="gameStore.gameStatus === 'racing'"
      />
      <!-- 第三列：玩家2赛车轨迹区（复用PlayerTrack组件） -->
      <PlayerTrack 
        :playerId="2"
        :position="gameStore.players[1].position"
        :speed="gameStore.players[1].speed"
        :distanceToEnd="gameStore.players[1].distanceToEnd"
        :isRacing="gameStore.gameStatus === 'racing'"
      />
      <!-- 第四列：玩家2答题区（复用PlayerAnswer组件） -->
      <PlayerAnswer 
        :playerId="2"
        :currentQuestion="gameStore.players[1].currentQuestion"
        :correctCount="gameStore.players[1].correctCount"
        :wrongCount="gameStore.players[1].wrongCount"
        @answer="handleAnswer(2)"
        :isRacing="gameStore.gameStatus === 'racing'"
      />
    </div>
    <!-- 控制区组件 -->
    <ControlArea 
      :gameStatus="gameStore.gameStatus"
      :loading="gameStore.loading"
      :errorMsg="gameStore.errorMsg"
      @startGame="gameStore.startGame"
      @restartGame="gameStore.resetGame"
      @retryFetch="gameStore.fetchQuestionPool"
    />
    <!-- 规则弹窗组件 -->
    <RuleModal v-model:visible="ruleVisible" />
    <!-- 结算弹窗组件 -->
    <ResultModal 
      v-model:visible="resultVisible"
      :winnerId="winnerId"
      :players="gameStore.players"
      @restart="gameStore.resetGame"
      @exit="gameStore.resetGame"
    />
  </div>
</template>

<script setup>
import { ref, watch } from "vue";
import { useGameStore } from "@/store/gameStore";
import Header from "@/components/Header.vue";
import PlayerAnswer from "@/components/PlayerAnswer.vue";
import PlayerTrack from "@/components/PlayerTrack.vue";
import ControlArea from "@/components/ControlArea.vue";
import RuleModal from "@/components/RuleModal.vue";
import ResultModal from "@/components/ResultModal.vue";

const gameStore = useGameStore();
const ruleVisible = ref(false);
const resultVisible = ref(false);
const winnerId = ref(null);

// 监听游戏状态，结束时显示结算弹窗
watch(
  () => gameStore.gameStatus,
  (newStatus) => {
    if (newStatus === "ended") {
      // 找到获胜者（先抵达终点的玩家）
      const winner = gameStore.players.find(p => p.position >= 800);
      winnerId.value = winner?.id;
      resultVisible.value = true;
    }
  }
);

// 处理答题事件
const handleAnswer = (playerId, selectedOption) => {
  const player = gameStore.players.find(p => p.id === playerId);
  if (!player || player.isAnswering) return;
  player.isAnswering = true;
  // 判断答题对错
  const isCorrect = selectedOption === player.currentQuestion.answer;
  if (isCorrect) {
    gameStore.correctAnswer(playerId);
  } else {
    gameStore.wrongAnswer(playerId);
  }
  // 分配新题目（从数据库返回的题目池中抽取）
  player.currentQuestion = gameStore.getRandomQuestion();
  // 解除答题锁定
  setTimeout(() => {
    player.isAnswering = false;
  }, 500);
};
</script>

<style scoped>
/* 四列布局样式 */
.game-main {
  display: flex;
  width: 100%;
  height: 70vh;
  gap: 10px;
  padding: 20px;
  box-sizing: border-box;
}
/* 答题区样式（第一、四列） */
.player-answer {
  width: calc(20% - 10px);
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: #f5f5f5;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}
/* 赛车轨迹区样式（第二、三列） */
.player-track {
  width: calc(30% - 10px);
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: #f5f5f5;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}
</style>
```

## 5.2 数据库
> （注：文档部分内容可能由 AI 生成）