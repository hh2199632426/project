<template>
  <div class="table-wrap">
    <div class="toolbar-container">
      <div class="search-bar">
        <input v-model="searchUsername" class="input" placeholder="搜索用户名" @keyup.enter="onSearch" />
        <button class="primary" @click="onSearch">搜索</button>
        <button class="reset" @click="onReset">重置</button>
      </div>
      <div class="toolbar">
        <input v-model="newUsername" class="input" placeholder="用户名" />
        <input v-model="newPassword" class="input" placeholder="密码" type="password" />
        <button class="primary" @click="onAdd">新增用户</button>
      </div>
    </div>
    <div class="table-container">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in adminStore.users" :key="item.id">
            <td>{{ item.id }}</td>
            <td>{{ item.username }}</td>
            <td>{{ formatTime(item.createdAt) }}</td>
            <td>
              <button class="link-btn" @click="onEdit(item)">修改</button>
              <button class="link-btn delete" @click="onDelete(item)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <div class="pagination">
      <span>共 {{ adminStore.usersTotal }} 条</span>
      <select v-model="pageSize" @change="handleSizeChange">
        <option :value="10">10条/页</option>
        <option :value="20">20条/页</option>
        <option :value="50">50条/页</option>
        <option :value="100">100条/页</option>
      </select>
      <button :disabled="adminStore.usersPage <= 1" @click="handlePageChange(adminStore.usersPage - 1)">上一页</button>
      <span>第 {{ adminStore.usersPage }} 页</span>
      <button :disabled="adminStore.usersPage * adminStore.usersSize >= adminStore.usersTotal" @click="handlePageChange(adminStore.usersPage + 1)">下一页</button>
    </div>

    <div v-if="showEditModal" class="modal-overlay">
      <div class="modal-content">
        <h3>修改用户</h3>
        <div class="form">
          <div class="form-item">
            <label>用户名</label>
            <input v-model="editUsername" class="input" placeholder="请输入用户名" />
          </div>
          <div class="form-item">
            <label>密码 (留空则不修改)</label>
            <input v-model="editPassword" class="input" placeholder="请输入新密码" type="password" />
          </div>
        </div>
        <div class="modal-actions">
          <button class="cancel" @click="showEditModal = false">取消</button>
          <button class="primary" @click="onUpdate">确认修改</button>
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
const searchUsername = ref("");
const newUsername = ref("");
const newPassword = ref("");
const pageSize = ref(10);
const showEditModal = ref(false);
const editUserId = ref(null);
const editUsername = ref("");
const editPassword = ref("");

const formatTime = (value) => {
  if (!value) {
    return "-";
  }
  return String(value).replace("T", " ");
};

const onAdd = async () => {
  const username = String(newUsername.value || "").trim();
  const password = String(newPassword.value || "").trim();
  if (!username) {
    uiStore.addToast("请输入用户名", "error");
    return;
  }
  if (!password) {
    uiStore.addToast("请输入密码", "error");
    return;
  }
  try {
    await adminStore.addUser(username, password);
    newUsername.value = "";
    newPassword.value = "";
    uiStore.addToast("新增成功");
  } catch (e) {
    const msg = e?.response?.data?.message || "新增失败";
    uiStore.addToast(msg, "error");
  }
};

const handlePageChange = (page) => {
  adminStore.fetchUsers(page, pageSize.value, searchUsername.value);
};

const handleSizeChange = () => {
  adminStore.fetchUsers(1, pageSize.value, searchUsername.value);
};

const onSearch = () => {
  adminStore.fetchUsers(1, pageSize.value, searchUsername.value);
};

const onReset = () => {
  searchUsername.value = "";
  adminStore.fetchUsers(1, pageSize.value, "");
};

const onDelete = async (item) => {
  const confirmed = await uiStore.showConfirm(`确定要删除用户 "${item.username}" 吗？`, "删除确认");
  if (!confirmed) {
    return;
  }
  try {
    await adminStore.removeUser(item.id);
    uiStore.addToast("删除成功");
  } catch (e) {
    const msg = e?.response?.data?.message || "删除失败";
    uiStore.addToast(msg, "error");
  }
};

const onEdit = (item) => {
  editUserId.value = item.id;
  editUsername.value = item.username;
  editPassword.value = "";
  showEditModal.value = true;
};

const onUpdate = async () => {
  const username = String(editUsername.value || "").trim();
  const password = String(editPassword.value || "").trim();
  if (!username) {
    uiStore.addToast("请输入用户名", "error");
    return;
  }
  try {
    await adminStore.updateUser(editUserId.value, username, password);
    showEditModal.value = false;
    uiStore.addToast("修改成功");
  } catch (e) {
    const msg = e?.response?.data?.message || "修改失败";
    uiStore.addToast(msg, "error");
  }
};

onMounted(async () => {
  await adminStore.fetchUsers(1, pageSize.value);
});
</script>

<style scoped>
.toolbar-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.search-bar {
  display: flex;
  gap: 10px;
  align-items: center;
}

.reset {
  border: 1px solid #dcdfe6;
  background: white;
  color: #606266;
  padding: 10px 14px;
  border-radius: 8px;
  cursor: pointer;
  white-space: nowrap;
}

.reset:hover {
  border-color: #c0c4cc;
  color: #303133;
}

.link-btn {
  border: none;
  background: transparent;
  color: #1677ff;
  cursor: pointer;
  padding: 4px 8px;
  font-size: 14px;
}

.link-btn:hover {
  color: #0f63d6;
}

.link-btn.delete {
  color: #ff4d4f;
}

.link-btn.delete:hover {
  color: #cf1322;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 12px;
}

.pagination select {
  padding: 6px;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
}

.pagination button {
  padding: 6px 12px;
  border: 1px solid #dcdfe6;
  background: white;
  border-radius: 4px;
  cursor: pointer;
}

.pagination button:disabled {
  background: #f5f7fa;
  cursor: not-allowed;
  color: #c0c4cc;
}

.table-wrap {
  background: white;
  border-radius: 10px;
  padding: 16px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
  height: calc(100vh - 100px);
  display: flex;
  flex-direction: column;
}

.toolbar {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-bottom: 12px;
}

.input {
  width: 200px;
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
  white-space: nowrap;
}

.primary:hover {
  background: #0f63d6;
}

table {
  width: 100%;
  border-collapse: collapse;
}

.table-container {
  flex: 1;
  overflow: auto;
}

th,
td {
  text-align: left;
  border-bottom: 1px solid #ebeef5;
  padding: 12px 8px;
}

th {
  color: #909399;
  font-weight: 500;
  position: sticky;
  top: 0;
  background: white;
  z-index: 1;
}

/* Modal Styles */
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
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  padding: 24px;
  width: 500px;
  max-width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.modal-content h3 {
  margin-top: 0;
  margin-bottom: 20px;
  font-size: 18px;
  color: #303133;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-item label {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.modal-actions {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.cancel {
  border: 1px solid #dcdfe6;
  background: white;
  color: #606266;
  padding: 10px 14px;
  border-radius: 8px;
  cursor: pointer;
}

.cancel:hover {
  border-color: #c0c4cc;
  color: #303133;
}
</style>
