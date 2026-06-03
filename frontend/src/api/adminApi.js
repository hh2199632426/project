import { gameConfig } from "../config/gameConfig";
import { http } from "./http";

export const adminLogin = (username, password) => {
  return http.post(gameConfig.api.adminLoginUrl, {
    username,
    password
  });
};

export const getAdminOverview = () => {
  return http.get(gameConfig.api.adminOverviewUrl);
};

export const getAdminUsers = (page = 1, size = 10, username) => {
  return http.get(gameConfig.api.adminUsersUrl, {
    params: {
      page,
      size,
      username
    }
  });
};

export const createAdminUser = (username, password) => {
  return http.post(gameConfig.api.adminUsersUrl, {
    username,
    password
  });
};

export const updateAdminUser = (userId, username, password) => {
  return http.put(`${gameConfig.api.adminUsersUrl}/${userId}`, {
    username,
    password
  });
};

export const deleteAdminUser = (userId) => {
  return http.delete(`${gameConfig.api.adminUsersUrl}/${userId}`);
};

export const getAdminQuestions = (page = 1, size = 10, categoryId, questionText) => {
  return http.get(gameConfig.api.adminQuestionsUrl, {
    params: {
      page,
      size,
      categoryId,
      questionText
    }
  });
};

export const createAdminQuestion = (payload) => {
  return http.post(gameConfig.api.adminQuestionsUrl, payload);
};

export const updateAdminQuestion = (questionId, payload) => {
  return http.put(`${gameConfig.api.adminQuestionsUrl}/${questionId}`, payload);
};

export const deleteAdminQuestion = (questionId) => {
  return http.delete(`${gameConfig.api.adminQuestionsUrl}/${questionId}`);
};

export const getAdminCategories = () => {
  return http.get(gameConfig.api.adminCategoriesUrl);
};

export const createAdminCategory = (name) => {
  return http.post(gameConfig.api.adminCategoriesUrl, {
    name
  });
};

export const deleteAdminCategory = (categoryId) => {
  return http.delete(`${gameConfig.api.adminCategoriesUrl}/${categoryId}`);
};
