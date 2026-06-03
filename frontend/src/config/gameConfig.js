const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || "/api";

// 1. 游戏配置模型
export const gameConfig = {
  modes: {
    short: { label: "短途", trackLength: 1000, questionCount: 50 },
    normal: { label: "正常", trackLength: 2000, questionCount: 100 },
    long: { label: "长途", trackLength: 5000, questionCount: 250 }
  },
  modeTypes: {
    normal: { label: "普通模式" },
    timeLimit: { label: "限时模式", timeLimitSeconds: 10 }
  },
  defaultModeKey: "normal",
  defaultModeType: "normal",
  initialSpeed: 10,          // 初始速度（px/s）
  maxSpeed: 60,              // 最大速度（px/s）
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
    questionUrl: `${apiBaseUrl}/questions`, // 后端接口地址
    loginUrl: `${apiBaseUrl}/login`,
    adminLoginUrl: `${apiBaseUrl}/admin/login`,
    adminOverviewUrl: `${apiBaseUrl}/admin/overview`,
    adminUsersUrl: `${apiBaseUrl}/admin/users`,
    adminQuestionsUrl: `${apiBaseUrl}/admin/questions`,
    adminCategoriesUrl: `${apiBaseUrl}/admin/categories`
  },
  gameStatus: "ready"        // 游戏状态：ready（准备中）、loading（题目加载中）、racing（竞速中）、ended（已结束）
};
