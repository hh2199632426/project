<template>
  <transition name="modal-fade">
    <div v-if="uiStore.confirmState.show" class="modal-overlay" @click.self="handleCancel">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ uiStore.confirmState.title }}</h3>
          <button class="close-btn" @click="handleCancel">×</button>
        </div>
        <div class="modal-body">
          <p>{{ uiStore.confirmState.message }}</p>
        </div>
        <div class="modal-actions">
          <button class="cancel" @click="handleCancel">取消</button>
          <button class="primary" @click="handleConfirm">确认</button>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { useUiStore } from '../../store/uiStore';

const uiStore = useUiStore();

const handleCancel = () => {
  if (uiStore.confirmState.onCancel) {
    uiStore.confirmState.onCancel();
  } else {
    uiStore.closeConfirm();
  }
};

const handleConfirm = () => {
  if (uiStore.confirmState.onConfirm) {
    uiStore.confirmState.onConfirm();
  }
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  backdrop-filter: blur(2px);
}

.modal-content {
  background: white;
  border-radius: 12px;
  padding: 24px;
  width: 420px;
  max-width: 90%;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
  gap: 16px;
  transform: translateY(0);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.close-btn {
  background: transparent;
  border: none;
  font-size: 24px;
  color: #909399;
  cursor: pointer;
  padding: 0;
  line-height: 1;
}

.close-btn:hover {
  color: #606266;
}

.modal-body {
  font-size: 15px;
  color: #606266;
  line-height: 1.6;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 8px;
}

.cancel {
  border: 1px solid #dcdfe6;
  background: white;
  color: #606266;
  padding: 8px 20px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.cancel:hover {
  border-color: #c0c4cc;
  color: #303133;
  background: #f5f7fa;
}

.primary {
  border: none;
  background: #1677ff;
  color: white;
  padding: 8px 20px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.primary:hover {
  background: #0f63d6;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(22, 119, 255, 0.3);
}

/* Animations */
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.3s ease;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

.modal-fade-enter-active .modal-content {
  animation: modal-slide-in 0.3s cubic-bezier(0.18, 0.89, 0.32, 1.28);
}

.modal-fade-leave-active .modal-content {
  animation: modal-slide-out 0.2s ease-in;
}

@keyframes modal-slide-in {
  from {
    opacity: 0;
    transform: scale(0.9) translateY(-20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

@keyframes modal-slide-out {
  from {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
  to {
    opacity: 0;
    transform: scale(0.9) translateY(20px);
  }
}
</style>
