import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useUiStore = defineStore('ui', () => {
  // Toast
  const toasts = ref([]);
  const addToast = (message, type = 'success', duration = 3000) => {
    const id = Date.now();
    toasts.value.push({ id, message, type });
    setTimeout(() => {
      removeToast(id);
    }, duration);
  };
  const removeToast = (id) => {
    toasts.value = toasts.value.filter(t => t.id !== id);
  };

  // Confirm Dialog
  const confirmState = ref({
    show: false,
    title: '提示',
    message: '',
    onConfirm: null,
    onCancel: null
  });

  const showConfirm = (message, title = '提示') => {
    return new Promise((resolve, reject) => {
      confirmState.value = {
        show: true,
        title,
        message,
        onConfirm: () => {
          confirmState.value.show = false;
          resolve(true);
        },
        onCancel: () => {
          confirmState.value.show = false;
          resolve(false);
        }
      };
    });
  };

  const closeConfirm = () => {
    confirmState.value.show = false;
  };

  return {
    toasts,
    addToast,
    removeToast,
    confirmState,
    showConfirm,
    closeConfirm
  };
});
