<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '80px'  }"
    >
      <p>
        <a-form
            layout="inline"
            :model="param"
        >
          <a-form-item>
            <a-input v-model:value="param.loginName" placeholder="登录名" />
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="handleQuery({ page:1,size:pagination.pageSize})" >查询</a-button>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="add()">添加</a-button>
          </a-form-item>
        </a-form>
      </p>

      <!--列,key id,数据user,分页,等待框,分页执行方法-->
      <a-table
          :columns="columns"
          :row-key="record=>record.id"
          :data-source="users"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
      >
        <template v-slot:action="{ text, record }">
          <a-space size="small">
            <a-button type="primary" @click="resetPassword(record)">
              重置密码
            </a-button>
            <a-button type="primary" @click="edit(record)">
              编辑
            </a-button>
            <a-popconfirm
                title="删了就真的没有啦,您是不是点歪了"
                ok-text="真删不跟你闹"
                cancel-text="哎哟点错了"
                @confirm="handleDelete(record.id)"
            >
              <a-button type="primary">
                删除
              </a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </a-table>
    </a-layout-content>

  </a-layout>
  <a-modal
      title="用户"
      v-model:visible="modalVisible"
      :confirm-loading="modalLoading"
      ok-text="确定"
      @ok="handleModalOk"
  >
    <!--弹出表单-->
    <a-form :model="user" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="用户名">
        <a-input v-model:value="user.loginName" :disabled="!!user.id"/>
      </a-form-item>
      <a-form-item label="昵称">
        <a-input v-model:value="user.name"/>
      </a-form-item>
      <a-form-item label="密码">
        <a-input v-model:value="user.password" type="password"/>
      </a-form-item>
    </a-form>
  </a-modal>
  <a-modal
      title="重置密码"
      v-model:visible="resetModalVisible"
      :confirm-loading="resetModalLoading"
      @ok="handleResetModalOk">
    <a-form :model="user" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="新密码">
        <a-input v-model:value="user.password" type="password"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script lang="ts">
import {defineComponent, onMounted, ref} from 'vue';//写上onMounted VUE3.0 setup集成了 导入ref 做响应式数据
import axios from 'axios';
import {message} from 'ant-design-vue';
import {Tool} from "@/util/tool";

declare let hexMd5: any;
declare let KEY: any;

export default defineComponent({
  name: 'AdminUser',
  setup() {

    const param = ref();
    param.value = {};
    const users = ref();//响应式数据 获取的书籍实时反馈到页面上
    const pagination = ref({
      current: 1,//当前页
      pageSize: 10,//分页条数
      total: 0
    });

    const loading = ref(false);

    const columns = [//页面的响应变量 不是数据的响应变量 代表就是这个表格里面有多少个数据 下面数据我们自己定义的
      {
        title: '登录名',
        dataIndex: 'loginName',
      },
      {
        title: '昵称',
        dataIndex: 'name',
      },
      {
        title: '密码',
        dataIndex: 'password',
      },
      {
        title: 'Action',
        key: 'action',
        slots: {customRender: 'action'}
      }
    ];

    /**
     * 数据查询
     **/
    const handleQuery = (params: any) => {
      loading.value = true;
      //如果不清空现有数据,则编辑保存重新加载数据后,再点编辑，则列表显示还是原来的数据
      users.value=[];
      axios.get("/user/list", {
        params:{
          page:params.page,
          size:params.size,
          loginName:param.value.loginName,//是否有value,没有就不传
        }
      }).then((response) => {
        loading.value = false;
        const data = response.data;
        if (data.success){
          users.value = data.content.list;
          //重置分页按钮
          pagination.value.current = params.page;//点第二页的按钮的时候前端 不会刷新 还是第一页的地方 实际我们以及到第二页了
          pagination.value.total=data.content.total;
        }else{
          message.error(data.message);
        }
      });
    };
    /**
     * 表格点击页码时触发
     */
    const handleTableChange = (pagination: any) => {
      console.log("看看自带的分页参数都有啥：" + pagination);
      handleQuery({
        page: pagination.current,
        size: pagination.pageSize
      });
    };

    /**表单***/
    /**
     * 数组[100,101]对应: 前端开发/ Vue
     */

    const user=ref();
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const handleModalOk = () => {
      modalLoading.value = true;

      //加密
     user.value.password = hexMd5(user.value.password + KEY);


      axios.post("/user/save",user.value).then((response) => {
        modalLoading.value = false;
        const data = response.data;  //commonResp
        if(data.success){
          modalVisible.value = false;

          //重新加载列表
          handleQuery({
            page:pagination.value.current,  //查询当前所在的页
            size:pagination.value.pageSize
          });
        }else{
          message.error(data.message);
        }
      });
    };
    /**
     * 编辑
     */
    const edit = ( record:any ) =>{
      modalVisible .value = true;
      user.value = Tool.copy(record);  //通过JSON对象转换来生成新的对象，从而不会直接更改到原来所显示的对象

    };
    /**
     * 添加
     */
    const add = () =>{
      modalVisible .value = true;
      user.value={};
    };


    /**
     * 删除
     */
    const handleDelete = ( id : number ) =>{

      axios.delete("/user/delete/"+id).then((response)=>{
        const data = response.data;  //commonResp
        if(data.success){
          //重新加载列表
          handleQuery({
            page:pagination.value.current,  //查询当前所在的页
            size:pagination.value.pageSize
          });
        }else{
          message.error(data.message);
        }
      });
    };

    // -------- 重置密码 ---------
    const resetModalVisible = ref(false);
    const resetModalLoading = ref(false);
    const handleResetModalOk = () => {
      resetModalLoading.value = true;

   //   user.value.password = hexMd5(user.value.password + KEY);

      axios.post("/user/reset-password", user.value).then((response) => {
        resetModalLoading.value = false;
        const data = response.data; // data = commonResp
        if (data.success) {
          resetModalVisible.value = false;

          // 重新加载列表
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize,
          });
        } else {
          message.error(data.message);
        }
      });
    };

    /**
     * 重置密码
     */
    const resetPassword = (record: any) => {
      resetModalVisible.value = true;
      user.value = Tool.copy(record);
      user.value.password = null;
    };

    onMounted(() => {
      handleQuery({
        page: 1,
        size: pagination.value.pageSize,
      });
    });


    return {
      users,//表格
      pagination,
      columns,
      loading,
      handleTableChange,


      edit,
      add,
      handleDelete,
      handleQuery,


      param,
      user,
      modalVisible,
      modalLoading,
      handleModalOk,

      resetModalVisible,
      resetModalLoading,
      handleResetModalOk,
      resetPassword,
    }
  }
});
</script>

<!-- #scoped表示当前组件才有用 -->
<style scoped>
.img-wh {
  width: 50px;
  height: 50px;
  line-height: 50px;
  border-radius: 8%;
  margin: 5px 0;
}

</style>