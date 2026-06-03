<template>
  <AdminLoginView v-if="!adminStore.isLoggedIn" />
  <AdminLayout v-else />
  <Toast />
</template>

<script setup>
import { onBeforeUnmount, onMounted, watch } from "vue";
import { useAdminStore } from "../../store/adminStore";
import { useUiStore } from "../../store/uiStore";
import AdminLoginView from "./AdminLoginView.vue";
import AdminLayout from "./AdminLayout.vue";
import Toast from "../../components/common/Toast.vue";
import { createInactivityLogout } from "../../utils/inactivityLogout";

const adminStore = useAdminStore();
const uiStore = useUiStore();

const inactivity = createInactivityLogout({
  timeoutMs: 30 * 60 * 1000,
  lastActivityKey: "adminLastActivityAt",
  authKey: "adminToken",
  onTimeout: () => {
    if (!adminStore.isLoggedIn) return;
    adminStore.logout();
    uiStore.addToast("长时间未操作，请重新登录", "info");
  }
});

const onUnauthorized = (event) => {
  const mode = event?.detail?.mode;
  if (mode !== "admin") return;
  if (!adminStore.isLoggedIn) return;
  adminStore.logout();
  uiStore.addToast("登录已失效，请重新登录", "info");
};

watch(
  () => adminStore.isLoggedIn,
  (isLoggedIn) => {
    if (isLoggedIn) {
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
