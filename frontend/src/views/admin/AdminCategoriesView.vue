<template>
  <div class="page">
    <div class="toolbar">
      <input v-model="newName" class="input" placeholder="输入目录名称" />
      <button class="primary" @click="onAdd">新增目录</button>
    </div>

    <div class="cards">
      <div v-for="item in adminStore.categories" :key="item.id" class="card" @click="onCategoryClick(item)">
        <div class="name">{{ item.name }}</div>
        <div class="count">{{ item.questionCount }}</div>
        <div class="footer">
          <div class="time">{{ formatTime(item.createdAt) }}</div>
          <button class="delete" @click.stop="onDelete(item)">删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { useAdminStore } from "../../store/adminStore";
import { useUiStore } from "../../store/uiStore";

const adminStore = useAdminStore();
const uiStore = useUiStore();
const newName = ref("");

const formatTime = (value) => {
  if (!value) {
    return "-";
  }
  return String(value).replace("T", " ");
};

const onAdd = async () => {
  const name = String(newName.value || "").trim();
  if (!name) {
    uiStore.addToast("请输入目录名称", "error");
    return;
  }
  try {
    await adminStore.addCategory(name);
    newName.value = "";
    uiStore.addToast("新增成功");
  } catch (e) {
    const msg = e?.response?.data?.message || "新增失败";
    uiStore.addToast(msg, "error");
  }
};

const onCategoryClick = (item) => {
  adminStore.setCurrentPage('questions', item.id);
};

const onDelete = async (item) => {
  const count = Number(item.questionCount || 0);
  const tip =
    count > 0
      ? `当前目录还有 ${count} 道题目，是否删除？删除后该目录下题目也会被删除。`
      : "是否删除当前目录？";
  
  const confirmed = await uiStore.showConfirm(tip, "删除确认");
  if (!confirmed) {
    return;
  }
  try {
    await adminStore.removeCategory(item.id);
    uiStore.addToast("删除成功");
  } catch (e) {
    const msg = e?.response?.data?.message || "删除失败";
    uiStore.addToast(msg, "error");
  }
};

onMounted(async () => {
  await adminStore.fetchCategories();
});
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.toolbar {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.input {
  width: 240px;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  padding: 10px 12px;
  outline: none;
}

.primary {
  border: none;
  background: #1677ff;
  color: white;
  padding: 10px 14px;
  border-radius: 8px;
  cursor: pointer;
}

.primary:hover {
  background: #0f63d6;
}

.cards {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  gap: 16px;
}

.card {
  background: white;
  border-radius: 10px;
  padding: 18px 14px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-height: 140px;
  align-items: center;
  text-align: center;
  transition: all 0.3s;
  cursor: pointer;
}

.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.name {
  font-size: 15px;
  font-weight: 700;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 100%;
}

.count {
  font-size: 32px;
  font-weight: 800;
  color: #1677ff;
}

.footer {
  margin-top: auto;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.time {
  color: #909399;
  font-size: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 100%;
}

.delete {
  border: 1px solid #ffccc7;
  background: #fff2f0;
  color: #cf1322;
  padding: 6px 14px;
  border-radius: 999px;
  cursor: pointer;
  font-size: 12px;
}

.delete:hover {
  background: #fff1f0;
  border-color: #ffa39e;
}
</style>
