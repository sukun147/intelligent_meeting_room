<template>
  <el-dialog
    :title="!dataForm.adminId ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
    <el-form-item label="用户名" prop="username">
      <el-input v-model="dataForm.username" placeholder="用户名"></el-input>
    </el-form-item>
    <el-form-item label="密码" prop="password">
      <el-input v-model="dataForm.password" placeholder="密码"></el-input>
    </el-form-item>
    <el-form-item label="手机号" prop="phoneNumber">
      <el-input v-model="dataForm.phoneNumber" placeholder="手机号"></el-input>
    </el-form-item>
    <el-form-item label="邮箱" prop="email">
      <el-input v-model="dataForm.email" placeholder="邮箱"></el-input>
    </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
  export default {
    data () {
      return {
        visible: false,
        dataForm: {
          adminId: 0,
          username: '',
          password: '',
          phoneNumber: '',
          email: ''
        },
        dataRule: {
          username: [
            { required: true, message: '用户名不能为空', trigger: 'blur' }
          ],
          password: [
            { required: true, message: '密码不能为空', trigger: 'blur' }
          ],
          phoneNumber: [
            { required: true, message: '手机号不能为空', trigger: 'blur' }
          ],
          email: [
            { required: true, message: '邮箱不能为空', trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      init (id) {
        this.dataForm.adminId = id || 0
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.adminId) {
            this.$http({
              url: this.$http.adornUrl(`/intelligent/admin/info/${this.dataForm.adminId}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.username = data.admin.username
                this.dataForm.password = data.admin.password
                this.dataForm.phoneNumber = data.admin.phoneNumber
                this.dataForm.email = data.admin.email
              }
            })
          }
        })
      },
      // 表单提交
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/intelligent/admin/${!this.dataForm.adminId ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'adminId': this.dataForm.adminId || undefined,
                'username': this.dataForm.username,
                'password': this.dataForm.password,
                'phoneNumber': this.dataForm.phoneNumber,
                'email': this.dataForm.email
              })
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.visible = false
                    this.$emit('refreshDataList')
                  }
                })
              } else {
                this.$message.error(data.msg)
              }
            })
          }
        })
      }
    }
  }
</script>
