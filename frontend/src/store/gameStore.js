// 2. Vue状态管理（使用Pinia，响应式管理数据）
import { defineStore } from "pinia";
import { getQuestionPool, getCategories } from "../api/questionApi"; // 从接口获取数据库题目
import { gameConfig } from "../config/gameConfig";

const getModeConfig = (modeKey) => {
  return gameConfig.modes?.[modeKey] || gameConfig.modes?.[gameConfig.defaultModeKey];
};

export const useGameStore = defineStore("game", {
  state: () => ({
    selectedModeKey: gameConfig.defaultModeKey,
    selectedModeType: gameConfig.defaultModeType,
    selectedCategories: [], // 选中的题目目录，空数组表示全选
    categories: [],         // 所有可用目录
    trackLength: getModeConfig(gameConfig.defaultModeKey)?.trackLength ?? 800,
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
        distanceToEnd: getModeConfig(gameConfig.defaultModeKey)?.trackLength ?? 800, // 距离终点距离
        timeLeft: 0,           // 剩余时间（秒）
        feedback: null         // 反馈信息 { type: 'correct'|'wrong'|'timeout', timestamp: number }
      },
      {
        id: 2,
        position: 0,
        speed: gameConfig.initialSpeed,
        correctCount: 0,
        wrongCount: 0,
        currentQuestion: null,
        isAnswering: false,
        distanceToEnd: getModeConfig(gameConfig.defaultModeKey)?.trackLength ?? 800,
        timeLeft: 0,
        feedback: null
      }
    ],
    gameStatus: gameConfig.gameStatus,
    questionPool: [],         // 从数据库获取的题目池（响应式，无本地存储）
    timer: null,              // 定时器
    loading: false,           // 题目加载状态
    errorMsg: ""              // 错误提示（如题目加载失败）
  }),
  actions: {
    setMode(modeKey, modeType = 'normal') {
      if (!getModeConfig(modeKey)) return;
      if (this.gameStatus !== "ready") return;
      this.selectedModeKey = modeKey;
      this.selectedModeType = modeType;
      this.trackLength = getModeConfig(modeKey)?.trackLength ?? this.trackLength;
      this.resetGame();
    },
    // 获取题目目录
    async fetchCategories() {
      try {
        const res = await getCategories();
        this.categories = res.data;
      } catch (err) {
        console.error("获取题目目录失败：", err);
      }
    },
    // 设置选中的目录
    setSelectedCategories(categoryIds) {
      this.selectedCategories = categoryIds;
    },
    // 从数据库获取题目池（通过后端接口）
    async fetchQuestionPool(count) {
      this.loading = true;
      this.errorMsg = "";
      try {
        const res = await getQuestionPool(count, this.selectedCategories); // 调用接口，后端从数据库查询题目
        this.questionPool = res.data; // 存储数据库返回的题目池
        if (count && this.questionPool.length < count) {
          console.warn(`数据库中题目数量不足${count}道，建议补充`);
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
      player.feedback = { type: 'correct', timestamp: Date.now() }; // 添加反馈
    },
    // 答错题处理
    wrongAnswer(playerId) {
      const player = this.players.find(p => p.id === playerId);
      if (!player) return;
      player.wrongCount++;
      player.speed = Math.max(player.speed - gameConfig.decelerateStep, gameConfig.minSpeed);
      player.feedback = { type: 'wrong', timestamp: Date.now() }; // 添加反馈
    },
    // 更新赛车位置和距离终点距离（响应式更新）
    updatePosition(playerId) {
      const player = this.players.find(p => p.id === playerId);
      if (!player) return;
      const distance = player.speed * (gameConfig.updateInterval / 1000);
      player.position = Math.min(player.position + distance, this.trackLength);
      player.distanceToEnd = Math.max(this.trackLength - player.position, 0);
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
        player.distanceToEnd = this.trackLength;
        player.timeLeft = 0;
      });
      this.gameStatus = "ready";
      if (this.timer) clearInterval(this.timer);
      this.timer = null;
    },
    // 开始游戏（先获取题目，再启动游戏）
    async startGame() {
      if (this.gameStatus !== "ready") return;
      this.gameStatus = "loading";
      const mode = getModeConfig(this.selectedModeKey) || getModeConfig(gameConfig.defaultModeKey);
      this.trackLength = mode?.trackLength ?? this.trackLength;
      this.players.forEach(player => {
        player.position = 0;
        player.distanceToEnd = this.trackLength;
      });

      await this.fetchQuestionPool(mode?.questionCount); // 从数据库获取题目
      if (this.questionPool.length === 0) {
        this.gameStatus = "ready";
        this.errorMsg = "无可用题目，请联系管理员补充数据库题目";
        return;
      }
      // 分配初始题目
      this.players.forEach(player => {
        player.currentQuestion = this.getRandomQuestion();
        if (this.selectedModeType === 'timeLimit') {
          player.timeLeft = gameConfig.modeTypes.timeLimit.timeLimitSeconds;
        }
      });
      // 启动定时器，更新位置和树木动态
      this.gameStatus = "racing";
      this.timer = setInterval(() => {
        this.players.forEach(player => {
          this.updatePosition(player.id);
          
          // 限时模式逻辑
          if (this.selectedModeType === 'timeLimit' && player.currentQuestion && !player.isAnswering) {
            player.timeLeft -= (gameConfig.updateInterval / 1000);
            if (player.timeLeft <= 0) {
              player.timeLeft = 0;
              this.handleTimeout(player.id);
            }
          }

          // 检查是否抵达终点
          if (player.position >= this.trackLength) {
            this.endGame(player.id);
          }
        });
      }, gameConfig.updateInterval);
    },
    // 超时处理
    handleTimeout(playerId) {
      this.wrongAnswer(playerId);
      const player = this.players.find(p => p.id === playerId);
      if (player) {
        player.feedback = { type: 'timeout', timestamp: Date.now() }; // 覆盖为超时反馈
        // 立即分配新题目
        player.currentQuestion = this.getRandomQuestion();
        player.timeLeft = gameConfig.modeTypes.timeLimit.timeLimitSeconds;
      }
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
      if (this.timer) clearInterval(this.timer);
      this.timer = null;
      // 后续可添加结算逻辑，如弹出结算弹窗
    }
  }
});
