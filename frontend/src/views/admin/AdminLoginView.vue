<template>
  <div class="login-container">
    <div class="login-box">
      <h2>管理端登录</h2>
      <div class="input-group">
        <label>账号</label>
        <input v-model="username" type="text" placeholder="请输入管理员账号" />
      </div>
      <div class="input-group">
        <label>密码</label>
        <input v-model="password" type="password" placeholder="请输入管理员密码" />
      </div>
      <button @click="handleLogin" :disabled="loading">登录</button>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useAdminStore } from "../../store/adminStore";

const username = ref("");
const password = ref("");
const loading = ref(false);
const errorMsg = ref("");
const adminStore = useAdminStore();

const handleLogin = async () => {
  if (!username.value || !password.value) {
    errorMsg.value = "请输入账号和密码";
    return;
  }
  loading.value = true;
  errorMsg.value = "";
  const success = await adminStore.login(username.value, password.value);
  if (!success) {
    errorMsg.value = adminStore.errorMsg || "登录失败";
  }
  loading.value = false;
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}

.login-box {
  background: white;
  padding: 40px;
  border-radius: 10px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  width: 320px;
}

h2 {
  margin: 0 0 20px;
  text-align: center;
}

.input-group {
  margin-bottom: 14px;
}

label {
  display: block;
  margin-bottom: 6px;
  color: #606266;
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  box-sizing: border-box;
}

button {
  width: 100%;
  padding: 10px;
  border: none;
  border-radius: 6px;
  background: #409eff;
  color: white;
  cursor: pointer;
  font-size: 15px;
}

button:disabled {
  background: #a0cfff;
  cursor: not-allowed;
}

.error {
  color: #f56c6c;
  margin-top: 12px;
  font-size: 13px;
}
</style>
