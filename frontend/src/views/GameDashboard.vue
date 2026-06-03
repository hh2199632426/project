<template>
  <div class="dashboard-container">
    <div class="header">
      <h1>游戏大厅</h1>
      <div class="actions">
        <button @click="openCheckIn" class="checkin-btn">每日打卡</button>
        <button @click="openProfile" class="profile-btn">个人信息</button>
        <button @click="logout" class="logout-btn">退出登录</button>
      </div>
    </div>
    <div class="game-list">
      <div class="game-card" @click="selectGame('racing')">
        <div class="game-icon">🏎️</div>
        <h3>赛车竞速</h3>
        <p>回答问题，加速冲刺！</p>
      </div>
      <!-- Future games can be added here -->
    </div>
  </div>
</template>

<script setup>
import { useUserStore } from "../store/userStore";
import { useUiStore } from "../store/uiStore";

const emit = defineEmits(["select-game", "open-profile", "open-checkin"]);
const userStore = useUserStore();
const uiStore = useUiStore();

const selectGame = (gameId) => {
  emit("select-game", gameId);
};

const openProfile = () => {
  emit("open-profile");
};

const openCheckIn = () => {
  emit("open-checkin");
};

const logout = async () => {
  const confirmed = await uiStore.showConfirm('是否退出登录？', '提示');
  if (confirmed) {
    userStore.logout();
    uiStore.addToast('退出登录成功', 'success');
  }
};
</script>

<style scoped>
.dashboard-container {
  padding: 40px;
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
}

.actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.checkin-btn {
  padding: 8px 16px;
  background-color: #52c41a;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.profile-btn {
  padding: 8px 16px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.logout-btn {
  padding: 8px 16px;
  background-color: #ff4d4f;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.game-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.game-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s, box-shadow 0.2s;
}

.game-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.game-icon {
  font-size: 48px;
  margin-bottom: 10px;
}

h3 {
  margin: 10px 0;
  color: #333;
}

p {
  color: #666;
}
</style>
