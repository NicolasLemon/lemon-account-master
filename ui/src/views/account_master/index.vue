<!--
 * @Author: Nicolas·Lemon
 * @Date: 2023-04-07 09:59:42
 * @LastEditors: Nicolas·Lemon
 * @LastEditTime: 2023-04-24 14:08:20
 * @Description: 柠檬账号大师管理页面
-->
<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryForm"
      size="small"
      :inline="true"
      v-show="showSearch"
      label-width="68px"
    >
      <el-form-item label="节点名" prop="accountNodeName">
        <el-input
          v-model="queryParams.accountNodeName"
          placeholder="请输入节点名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="账号域名" prop="accountDomain">
        <el-input
          v-model="queryParams.accountDomain"
          placeholder="请输入账号域名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="账号说明" prop="accountInfo">
        <el-input
          v-model="queryParams.accountInfo"
          placeholder="请输入账号说明"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          icon="el-icon-search"
          size="mini"
          @click="handleQuery"
          >搜索</el-button
        >
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery"
          >重置</el-button
        >
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8" type="flex">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          >新增</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-sort"
          size="mini"
          @click="toggleExpandAll"
          >展开/折叠</el-button
        >
      </el-col>

      <el-col :span="1.5" style="margin: 0 auto">
        有效记录 {{ total }} 条</el-col
      >

      <right-toolbar
        :showSearch.sync="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row>

    <el-table
      v-if="refreshTable"
      v-loading="loading"
      :data="accountList"
      row-key="accountId"
      :default-expand-all="isExpandAll"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    >
      <el-table-column label="节点名" prop="accountNodeName" />
      <el-table-column label="用户名" align="center" prop="accountUserName" />
      <el-table-column label="密码" align="center" prop="accountUserPwd" />
      <el-table-column label="账号域名" align="center" prop="accountDomain" />
      <el-table-column label="账号说明" align="center" prop="accountInfo" />
      <el-table-column
        label="操作"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="scope">
          <el-tooltip placement="top" content="查看或隐藏密码">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-view"
              v-if="
                scope.row.accountUserName !== null ||
                scope.row.accountUserPwd !== null
              "
              @click="handlePwdView(scope.row)"
            ></el-button>
          </el-tooltip>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            >修改</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-plus"
            @click="handleAdd(scope.row)"
            >新增</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改柠檬账号大师账号对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="父级节点" prop="parentId">
          <treeselect
            v-model="form.parentId"
            :options="accountOptions"
            :normalizer="normalizer"
            placeholder="请选择父级节点"
          />
        </el-form-item>
        <el-form-item label="节 点 名" prop="accountNodeName">
          <el-input v-model="form.accountNodeName" placeholder="请输入节点名" />
        </el-form-item>
        <el-form-item label="用 户 名" prop="accountUserName">
          <el-input v-model="form.accountUserName" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密　　码" prop="accountUserPwd">
          <el-input
            v-model="form.accountUserPwd"
            placeholder="请输入账号密码"
          />
        </el-form-item>
        <el-form-item label="账号域名" prop="accountDomain">
          <el-input v-model="form.accountDomain" placeholder="请输入账号域名" />
        </el-form-item>
        <el-form-item label="账号说明" prop="accountInfo">
          <el-input v-model="form.accountInfo" placeholder="请输入账号说明" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listAccount,
  getAccount,
  delAccount,
  addAccount,
  updateAccount,
} from "@/api/account/account";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";

export default {
  name: "Account",
  components: {
    Treeselect,
  },
  data() {
    return {
      // 行账号密码临时缓存列表
      rowPwdList: [],
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 柠檬账号大师账号表格数据
      accountList: [],
      // 总条数
      total: 0,
      // 柠檬账号大师账号树选项
      accountOptions: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否展开，默认全部展开
      isExpandAll: true,
      // 重新渲染表格状态
      refreshTable: true,
      // 查询参数
      queryParams: {
        parentId: null,
        accountNodeName: null,
        accountInfo: null,
        accountDomain: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        parentId: [
          { required: true, message: "父级节点不能为空", trigger: "blur" },
        ],
        accountNodeName: [
          { required: true, message: "节点名不能为空", trigger: "blur" },
        ],
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询柠檬账号大师账号列表 */
    getList() {
      this.loading = true;
      listAccount(this.queryParams).then((response) => {
        this.accountList = this.handleTree(
          response.rows,
          "accountId",
          "parentId"
        );
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 转换柠檬账号大师账号数据结构 */
    normalizer(node) {
      if (node.children && !node.children.length) {
        delete node.children;
      }
      return {
        id: node.accountId,
        label: node.accountNodeName,
        children: node.children,
      };
    },
    /** 查询柠檬账号大师账号下拉树结构 */
    getTreeselect() {
      listAccount().then((response) => {
        this.accountOptions = [];
        const data = {
          accountId: 0,
          accountNodeName: "顶级节点",
          children: [],
        };
        data.children = this.handleTree(response.rows, "accountId", "parentId");
        this.accountOptions.push(data);
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        accountId: null,
        parentId: null,
        accountUserName: null,
        accountUserPwd: null,
        accountInfo: null,
        accountDomain: null,
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 新增按钮操作 */
    handleAdd(row) {
      this.reset();
      this.getTreeselect();
      if (row != null && row.accountId) {
        this.form.parentId = row.accountId;
      } else {
        this.form.parentId = 0;
      }
      this.open = true;
      this.title = "添加柠檬账号大师账号";
    },
    /** 展开/折叠操作 */
    toggleExpandAll() {
      this.refreshTable = false;
      this.isExpandAll = !this.isExpandAll;
      this.$nextTick(() => {
        this.refreshTable = true;
      });
    },
    /** 显示/隐藏密码 */
    handlePwdView(row) {
      // 当前行的用户名和密码
      let currents = {
        name: row.accountUserName,
        pwd: row.accountUserPwd,
      };

      // 为空就不处理
      if (currents.name === null && currents.pwd === null) {
        return;
      }

      // 假密码，真密码
      let fakes = { name: null, pwd: null };
      let reals = { name: null, pwd: null };

      // 判断在rowPwdList是否存在当前行的数据
      for (let key in this.rowPwdList) {
        let item = this.rowPwdList[key];
        if (item.id === row.accountId) {
          fakes = item.fakes;
          reals = item.reals;
          break;
        }
      }

      // 隐藏真密码
      if (
        fakes.name !== null &&
        fakes.name !== currents.name &&
        fakes.pwd !== null &&
        fakes.pwd !== currents.pwd
      ) {
        row.accountUserName = fakes.name;
        row.accountUserPwd = fakes.pwd;
        return;
      }
      if (fakes.name !== null && fakes.name !== currents.name) {
        row.accountUserName = fakes.name;
        return;
      }
      if (fakes.pwd !== null && fakes.pwd !== currents.pwd) {
        row.accountUserPwd = fakes.pwd;
        return;
      }

      // 显示真密码
      if (
        reals.name !== null &&
        reals.name !== currents.name &&
        reals.pwd !== null &&
        reals.pwd !== currents.pwd
      ) {
        row.accountUserName = reals.name;
        row.accountUserPwd = reals.pwd;
      }
      if (reals.name !== null && reals.name !== currents.name) {
        row.accountUserName = reals.name;
        return;
      }
      if (reals.pwd !== null && reals.pwd !== currents.pwd) {
        row.accountUserPwd = reals.pwd;
        return;
      }

      // 第一次点击的时候就调用接口，反之就利用缓存显隐密码
      getAccount(row.accountId).then((response) => {
        reals = {
          name: response.data.accountUserName,
          pwd: response.data.accountUserPwd,
        };
        // 将当前行的数据插入到临时缓存列表中去
        this.rowPwdList.push({
          id: row.accountId,
          fakes: { name: currents.name, pwd: currents.pwd },
          reals: reals,
        });
        // 显示真密码
        row.accountUserName = reals.name;
        row.accountUserPwd = reals.pwd;
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      this.getTreeselect();
      if (row != null) {
        this.form.parentId = row.accountId;
      }
      getAccount(row.accountId).then((response) => {
        this.form = { ...response.data };
        this.open = true;
        this.title = "修改柠檬账号大师账号";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (!valid) {
          return;
        }
        if (this.form.accountId != null) {
          updateAccount(this.form).then((response) => {
            this.$modal.msgSuccess("修改成功");
            this.removeObjFromRowPwdList(this.form.accountId);
            this.open = false;
            this.getList();
          });
        } else {
          addAccount(this.form).then((response) => {
            this.$modal.msgSuccess("新增成功");
            this.open = false;
            this.getList();
          });
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      this.$modal
        .confirm('是否确认删除为 "' + row.accountNodeName + '" 的数据项？')
        .then(function () {
          return delAccount(row.accountId);
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
          this.removeObjFromRowPwdList(row.accountId);
        })
        .catch(() => {});
    },
    // 移除相关账号id的临时缓存
    removeObjFromRowPwdList(accountId) {
      const idx = this.rowPwdList.findIndex((m) => m.id == accountId);
      if (idx >= 0) {
        this.rowPwdList.splice(idx, 1);
      }
    },
  },
};
</script>

<style lang="scss">
.el-tooltip__popper[x-placement^="top"] {
  opacity: 0.95;
}
</style>