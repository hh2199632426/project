<template>
  <div class="admin-layout">
    <aside class="sider">
      <h3 class="title">管理端</h3>
      <button :class="{ active: adminStore.currentPage === 'home' }" @click="adminStore.setCurrentPage('home')">首页</button>
      <button :class="{ active: adminStore.currentPage === 'categories' }" @click="adminStore.setCurrentPage('categories')">目录管理</button>
      <button :class="{ active: adminStore.currentPage === 'users' }" @click="adminStore.setCurrentPage('users')">用户管理</button>
      <button :class="{ active: adminStore.currentPage === 'questions' }" @click="adminStore.setCurrentPage('questions')">题目管理</button>
      <button class="logout" @click="handleLogout">退出登录</button>
    </aside>
    <main class="content">
      <h2 class="page-title">{{ pageTitle }}</h2>
      <Transition name="fade" mode="out-in">
        <component :is="currentComponent" :key="adminStore.currentPage" />
      </Transition>
    </main>

    <!-- Global UI Components -->
    <ConfirmDialog />
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useAdminStore } from "../../store/adminStore";
import { useUiStore } from "../../store/uiStore";
import AdminHomeView from "./AdminHomeView.vue";
import AdminCategoriesView from "./AdminCategoriesView.vue";
import AdminUsersView from "./AdminUsersView.vue";
import AdminQuestionsView from "./AdminQuestionsView.vue";
import ConfirmDialog from "../../components/common/ConfirmDialog.vue";

const adminStore = useAdminStore();
const uiStore = useUiStore();

const pageTitle = computed(() => {
  if (adminStore.currentPage === "categories") {
    return "目录管理";
  }
  if (adminStore.currentPage === "users") {
    return "用户管理";
  }
  if (adminStore.currentPage === "questions") {
    return "题目管理";
  }
  return "首页";
});

const currentComponent = computed(() => {
  switch (adminStore.currentPage) {
    case 'categories': return AdminCategoriesView;
    case 'users': return AdminUsersView;
    case 'questions': return AdminQuestionsView;
    default: return AdminHomeView;
  }
});

const handleLogout = async () => {
  const confirmed = await uiStore.showConfirm('是否退出登录？', '提示');
  if (confirmed) {
    adminStore.logout();
    uiStore.addToast('退出登录成功', 'success');
  }
};
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: #f5f7fa;
}

.sider {
  width: 220px;
  background: #001529;
  padding: 24px 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  height: 100vh;
  box-sizing: border-box;
}

.title {
  color: white;
  margin: 0 0 16px;
}

button {
  text-align: left;
  border: none;
  background: transparent;
  color: #d9d9d9;
  padding: 10px 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
}

button:hover {
  background: rgba(255, 255, 255, 0.14);
}

button.active {
  background: #1677ff;
  color: white;
}

.logout {
  margin-top: auto;
  color: #ffccc7;
}

.content {
  flex: 1;
  padding: 24px;
  overflow: auto;
  height: 100vh;
  box-sizing: border-box;
  position: relative;
}

.page-title {
  margin-top: 0;
  margin-bottom: 16px;
}

/* Page Transitions */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
