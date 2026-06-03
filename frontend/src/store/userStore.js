import { defineStore } from "pinia";
import { login as loginApi } from "../api/authApi";
import { updateProfile as updateProfileApi, changePassword as changePasswordApi } from "../api/userApi";

export const useUserStore = defineStore("user", {
  state: () => ({
    user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : null,
    token: localStorage.getItem("userToken") || "",
    isLoggedIn: !!localStorage.getItem("userToken"),
    errorMsg: ""
  }),
  actions: {
    async login(username, password) {
      this.errorMsg = "";
      try {
        const response = await loginApi(username, password);
        if (response.data && response.data.user && response.data.token) {
          this.user = response.data.user;
          this.token = response.data.token;
          this.isLoggedIn = true;
          localStorage.setItem("user", JSON.stringify(this.user));
          localStorage.setItem("userToken", this.token);
          return true;
        }
      } catch (error) {
        this.errorMsg = "登录失败：用户名或密码错误";
        console.error("Login failed:", error);
        return false;
      }
      return false;
    },
    async updateProfile(nickName) {
      try {
        await updateProfileApi(nickName);
        if (this.user) {
          this.user.nickName = nickName;
          localStorage.setItem("user", JSON.stringify(this.user));
        }
        return true;
      } catch (error) {
        console.error("Update profile failed:", error);
        return false;
      }
    },
    async changePassword(oldPassword, newPassword) {
      try {
        await changePasswordApi(oldPassword, newPassword);
        return true;
      } catch (error) {
        console.error("Change password failed:", error);
        throw error;
      }
    },
    logout() {
      this.user = null;
      this.token = "";
      this.isLoggedIn = false;
      localStorage.removeItem("user");
      localStorage.removeItem("userToken");
      localStorage.removeItem("userLastActivityAt");
    }
  }
});
