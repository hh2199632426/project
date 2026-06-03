<template>
  <div class="toast-container">
    <transition-group name="toast-list" tag="div">
      <div v-for="toast in uiStore.toasts" :key="toast.id" class="toast-item" :class="toast.type">
        <div class="toast-icon">
          <span v-if="toast.type === 'success'">✔️</span>
          <span v-else-if="toast.type === 'error'">✖️</span>
          <span v-else>ℹ️</span>
        </div>
        <div class="toast-content">{{ toast.message }}</div>
      </div>
    </transition-group>
  </div>
</template>

<script setup>
import { useUiStore } from '../../store/uiStore';

const uiStore = useUiStore();
</script>

<style scoped>
.toast-container {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.toast-item {
  min-width: 300px;
  padding: 12px 16px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  font-size: 14px;
  transition: all 0.3s ease;
}

.toast-item.success {
  background: #f6ffed;
  border: 1px solid #b7eb8f;
  color: #52c41a;
}

.toast-item.error {
  background: #fff2f0;
  border: 1px solid #ffccc7;
  color: #ff4d4f;
}

.toast-item.info {
  background: #e6f7ff;
  border: 1px solid #91caff;
  color: #1890ff;
}

.toast-icon {
  font-size: 18px;
}

/* Animations */
.toast-list-enter-active,
.toast-list-leave-active {
  transition: all 0.4s ease;
}

.toast-list-enter-from,
.toast-list-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>
