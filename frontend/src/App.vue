<template>
  <div v-if="isAdminMode" class="app-container">
    <AdminApp />
  </div>
  <div v-else class="app-container">
    <Transition :name="transitionName" mode="out-in">
      <div v-if="!userStore.isLoggedIn" key="login" class="app-container">
        <LoginView />
      </div>
      <div v-else-if="!selectedGame && !showProfile && !showCheckIn" key="dashboard" class="app-container">
        <GameDashboard @select-game="onSelectGame" @open-profile="onOpenProfile" @open-checkin="onOpenCheckIn" />
      </div>
      <div v-else-if="showProfile" key="profile" class="app-container">
        <ProfileView :initial-tab="profileInitialTab" :initial-medal-id="profileInitialMedalId" @back="backFromProfile" />
      </div>
      <div v-else-if="showCheckIn" key="checkin" class="app-container">
        <CheckInView @back="backFromCheckIn" @view-medal="onViewMedal" />
      </div>
      <div v-else key="game" class="app-container">
        <GameView @back="backToDashboard" />
      </div>
    </Transition>
    <Toast />
    <ConfirmDialog />
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue';
import { useUserStore } from './store/userStore';
import { useGameStore } from './store/gameStore';
import { useUiStore } from './store/uiStore';
import LoginView from './views/LoginView.vue';
import GameDashboard from './views/GameDashboard.vue';
import GameView from './views/GameView.vue';
import ProfileView from './views/ProfileView.vue';
import CheckInView from './views/CheckInView.vue';
import AdminApp from './views/admin/AdminApp.vue';
import Toast from './components/common/Toast.vue';
import ConfirmDialog from './components/common/ConfirmDialog.vue';
import { createInactivityLogout } from './utils/inactivityLogout';

const userStore = useUserStore();
const gameStore = useGameStore();
const uiStore = useUiStore();
const selectedGame = ref(null);
const showProfile = ref(false);
const showCheckIn = ref(false);
const isAdminMode = computed(() => window.location.pathname.startsWith('/admin') || window.location.hash === '#/admin');
const transitionName = ref('page-up');

// 监听状态变化，设置过渡动画方向
watch(
  [() => userStore.isLoggedIn, () => selectedGame.value, showProfile, showCheckIn],
  ([isLoggedIn, game, profile, checkIn], [prevIsLoggedIn, prevGame, prevProfile, prevCheckIn]) => {
    // 登录 -> 游戏大厅 (前进)
    if (isLoggedIn && !prevIsLoggedIn) {
      transitionName.value = 'page-up';
    } 
    // 退出登录 -> 登录页 (后退)
    else if (!isLoggedIn && prevIsLoggedIn) {
      transitionName.value = 'page-down';
    }
    // 游戏大厅 -> 游戏 (前进)
    else if (game && !prevGame) {
      transitionName.value = 'page-up';
    }
    // 游戏 -> 游戏大厅 (后退)
    else if (!game && prevGame) {
      transitionName.value = 'page-down';
    }
    // 游戏大厅 -> 个人中心 (前进)
    else if (profile && !prevProfile) {
      transitionName.value = 'page-up';
    }
    // 个人中心 -> 游戏大厅 (后退)
    else if (!profile && prevProfile) {
      transitionName.value = 'page-down';
    }
    // 游戏大厅 -> 每日打卡 (前进)
    else if (checkIn && !prevCheckIn) {
      transitionName.value = 'page-up';
    }
    // 每日打卡 -> 游戏大厅 (后退)
    else if (!checkIn && prevCheckIn) {
      transitionName.value = 'page-down';
    }
  }
);

const profileInitialTab = ref('info');
const profileInitialMedalId = ref(null);

const onSelectGame = (gameId) => {
  selectedGame.value = gameId;
};

const onOpenProfile = () => {
  profileInitialTab.value = 'info';
  profileInitialMedalId.value = null;
  showProfile.value = true;
};

const backFromProfile = () => {
  showProfile.value = false;
};

const onOpenCheckIn = () => {
  showCheckIn.value = true;
};

const backFromCheckIn = () => {
  showCheckIn.value = false;
};

const onViewMedal = (medalId) => {
  showCheckIn.value = false;
  profileInitialTab.value = 'medals';
  profileInitialMedalId.value = medalId;
  showProfile.value = true;
};

const backToDashboard = () => {
  selectedGame.value = null;
  gameStore.resetGame();
};

const inactivity = createInactivityLogout({
  timeoutMs: 30 * 60 * 1000,
  lastActivityKey: 'userLastActivityAt',
  authKey: 'userToken',
  onTimeout: () => {
    if (!userStore.isLoggedIn) return;
    userStore.logout();
    selectedGame.value = null;
    showProfile.value = false;
    showCheckIn.value = false;
    gameStore.resetGame();
    uiStore.addToast('长时间未操作，请重新登录', 'info');
  }
});

const onUnauthorized = (event) => {
  const mode = event?.detail?.mode;
  if (mode !== "user") return;
  if (!userStore.isLoggedIn) return;
  userStore.logout();
  selectedGame.value = null;
  showProfile.value = false;
  showCheckIn.value = false;
  gameStore.resetGame();
  uiStore.addToast('登录已失效，请重新登录', 'info');
};

watch(
  [() => userStore.isLoggedIn, isAdminMode],
  ([isLoggedIn, adminMode]) => {
    if (!adminMode && isLoggedIn) {
      inactivity.start();
    } else {
      inactivity.stop();
    }
  },
  { immediate: true }
);

onMounted(() => {
  window.addEventListener("auth:unauthorized", onUnauthorized);
});

onBeforeUnmount(() => {
  window.removeEventListener("auth:unauthorized", onUnauthorized);
  inactivity.stop();
});
</script>

<style>
body {
  margin: 0;
  padding: 0;
  overflow: hidden;
}
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  height: 100vh;
  width: 100vw;
}
.app-container {
  height: 100%;
  width: 100%;
}

/* 页面切换动画 */
.page-up-enter-active,
.page-up-leave-active {
  transition: opacity 0.4s ease, transform 0.4s ease;
}

.page-up-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.page-up-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

.page-down-enter-active,
.page-down-leave-active {
  transition: opacity 0.4s ease, transform 0.4s ease;
}

.page-down-enter-from {
  opacity: 0;
  transform: translateY(-20px);
}

.page-down-leave-to {
  opacity: 0;
  transform: translateY(20px);
}
</style>
