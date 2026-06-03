import { defineStore } from "pinia";
import {
  adminLogin,
  createAdminCategory,
  createAdminQuestion,
  createAdminUser,
  deleteAdminUser,
  updateAdminUser,
  deleteAdminCategory,
  getAdminCategories,
  getAdminOverview,
  getAdminQuestions,
  getAdminUsers,
  deleteAdminQuestion,
  updateAdminQuestion
} from "../api/adminApi";

export const useAdminStore = defineStore("admin", {
  state: () => ({
    admin: localStorage.getItem("adminUser") ? JSON.parse(localStorage.getItem("adminUser")) : null,
    token: localStorage.getItem("adminToken") || "",
    isLoggedIn: !!localStorage.getItem("adminToken"),
    errorMsg: "",
    currentPage: "home",
    overview: {
      questionCount: 0,
      userCount: 0,
      categoryCount: 0
    },
    users: [],
    usersPage: 1,
    usersSize: 10,
    usersTotal: 0,
    questions: [],
    questionsPage: 1,
    questionsSize: 10,
    questionsTotal: 0,
    categories: [],
    targetCategoryId: null
  }),
  actions: {
    async login(username, password) {
      this.errorMsg = "";
      try {
        const response = await adminLogin(username, password);
        if (response.data && response.data.admin && response.data.token) {
          this.admin = response.data.admin;
          this.token = response.data.token;
          this.isLoggedIn = true;
          localStorage.setItem("adminUser", JSON.stringify(this.admin));
          localStorage.setItem("adminToken", this.token);
          return true;
        }
      } catch (error) {
        this.errorMsg = "登录失败：管理员账号或密码错误";
        return false;
      }
      this.errorMsg = "登录失败：管理员账号或密码错误";
      return false;
    },
    logout() {
      this.admin = null;
      this.token = "";
      this.isLoggedIn = false;
      this.currentPage = "home";
      localStorage.removeItem("adminUser");
      localStorage.removeItem("adminToken");
      localStorage.removeItem("adminLastActivityAt");
    },
    async fetchOverview() {
      const response = await getAdminOverview();
      this.overview = response.data;
    },
    async fetchUsers(page = 1, size = 10, username) {
      const response = await getAdminUsers(page, size, username);
      this.users = response.data.content;
      this.usersPage = response.data.page;
      this.usersSize = response.data.size;
      this.usersTotal = response.data.total;
    },
    async addUser(username, password) {
      await createAdminUser(username, password);
      await this.fetchUsers(1, this.usersSize);
      await this.fetchOverview();
    },
    async updateUser(userId, username, password) {
      await updateAdminUser(userId, username, password);
      await this.fetchUsers(this.usersPage, this.usersSize);
      await this.fetchOverview();
    },
    async removeUser(userId) {
      await deleteAdminUser(userId);
      await this.fetchUsers(this.usersPage, this.usersSize);
      await this.fetchOverview();
    },
    async fetchQuestions(page = 1, size = 10, categoryId, questionText) {
      const response = await getAdminQuestions(page, size, categoryId, questionText);
      this.questions = response.data.content;
      this.questionsPage = response.data.page;
      this.questionsSize = response.data.size;
      this.questionsTotal = response.data.total;
    },
    async addQuestion(payload) {
      await createAdminQuestion(payload);
      await this.fetchQuestions(1, this.questionsSize);
      await this.fetchOverview();
    },
    async batchAddQuestions(questions) {
      for (const q of questions) {
        try {
          await createAdminQuestion(q);
        } catch (e) {
          console.error("Failed to add question:", q, e);
        }
      }
      await this.fetchQuestions(1, this.questionsSize);
      await this.fetchOverview();
    },
    async updateQuestion(questionId, payload) {
      await updateAdminQuestion(questionId, payload);
      await this.fetchQuestions(this.questionsPage, this.questionsSize);
      await this.fetchOverview();
    },
    async removeQuestion(questionId) {
      await deleteAdminQuestion(questionId);
      await this.fetchQuestions(this.questionsPage, this.questionsSize);
      await this.fetchOverview();
    },
    async fetchCategories() {
      const response = await getAdminCategories();
      this.categories = response.data;
    },
    async addCategory(name) {
      await createAdminCategory(name);
      await this.fetchCategories();
      await this.fetchOverview();
    },
    async removeCategory(categoryId) {
      await deleteAdminCategory(categoryId);
      await this.fetchCategories();
      await this.fetchOverview();
    },
    setCurrentPage(page, categoryId = null) {
      this.currentPage = page;
      this.targetCategoryId = categoryId;
    }
  }
});
