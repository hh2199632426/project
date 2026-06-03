// 3. 接口封装（Axios请求后端接口，从数据库获取题目）
import { gameConfig } from "../config/gameConfig";
import { http } from "./http";

// 获取数据库中的题目池
export const getQuestionPool = (count = 50, categoryIds = []) => {
  return http.get(gameConfig.api.questionUrl, {
    timeout: 10000, // 超时时间10s，确保数据库查询完成
    params: {
      count, // 请求指定数量题目，后端从数据库查询并返回
      categoryIds: categoryIds.length > 0 ? categoryIds.join(',') : undefined
    }
  });
};

export const getCategories = () => {
  return http.get('/api/categories');
};
