# Git 安装与项目上传指南

## 一、Git 安装

### Windows 系统

1. **下载 Git 安装包**
   - 访问 [Git 官网](https://git-scm.com/download/win)
   - 下载适合你系统的版本（64位或32位）

2. **安装 Git**
   - 运行下载的安装程序
   - 按照默认选项安装即可，建议勾选 "Add Git to PATH" 选项
   - 完成安装后，重启计算机

3. **验证安装**
   - 打开命令提示符（cmd）或 PowerShell
   - 输入 `git --version` 命令
   - 如果显示 Git 版本信息，则安装成功

## 二、初始化 Git 仓库

1. **打开项目目录**
   - 打开命令提示符（cmd）或 PowerShell
   - 切换到项目目录：
     ```bash
     cd E:\毕设\canteen-system
     ```

2. **初始化 Git 仓库**
   ```bash
   git init
   ```

3. **查看当前状态**
   ```bash
   git status
   ```

## 三、添加文件并提交

1. **添加所有文件**
   ```bash
   git add .
   ```

2. **提交文件**
   ```bash
   git commit -m "初始化项目"
   ```

## 四、在 GitHub 上创建仓库

1. **登录 GitHub**
   - 访问 [GitHub](https://github.com/)
   - 使用你的账号登录

2. **创建新仓库**
   - 点击右上角的 "+" 图标，选择 "New repository"
   - 填写仓库名称（例如：canteen-system）
   - 选择仓库类型（公开或私有）
   - 点击 "Create repository" 按钮

3. **复制仓库 URL**
   - 在创建成功的页面中，复制 HTTPS 或 SSH 地址

## 五、关联远程仓库

1. **添加远程仓库**
   ```bash
   git remote add origin <你的仓库URL>
   ```
   例如：
   ```bash
   git remote add origin https://github.com/yourusername/canteen-system.git
   ```

2. **查看远程仓库**
   ```bash
   git remote -v
   ```

## 六、推送到 GitHub

1. **推送代码**
   ```bash
   git push -u origin master
   ```
   或
   ```bash
   git push -u origin main
   ```

2. **输入 GitHub 账号信息**
   - 如果使用 HTTPS 地址，会提示输入 GitHub 用户名和密码
   - 如果使用 SSH 地址，需要先设置 SSH 密钥

## 七、常见问题解决

### 1. 推送失败 - 远程仓库不存在
- 检查仓库 URL 是否正确
- 确保 GitHub 上已创建对应仓库

### 2. 推送失败 - 权限不足
- 检查 GitHub 账号是否有权限
- 确保使用正确的认证方式

### 3. 推送失败 - 分支不存在
- 尝试使用 `git push -u origin main` 或 `git push -u origin master`

### 4. 推送速度慢
- 检查网络连接
- 考虑使用 SSH 方式推送

## 八、后续操作

### 1. 查看远程仓库
   ```bash
   git remote -v
   ```

### 2. 拉取最新代码
   ```bash
   git pull
   ```

### 3. 推送新的提交
   ```bash
   git add .
   git commit -m "更新内容"
   git push
   ```

### 4. 查看提交历史
   ```bash
   git log
   ```

## 九、SSH 密钥设置（可选）

1. **生成 SSH 密钥**
   ```bash
   ssh-keygen -t rsa -b 4096 -C "your_email@example.com"
   ```

2. **查看公钥**
   ```bash
   cat ~/.ssh/id_rsa.pub
   ```

3. **添加公钥到 GitHub**
   - 登录 GitHub
   - 进入 "Settings" -> "SSH and GPG keys"
   - 点击 "New SSH key"
   - 粘贴公钥内容并保存

4. **测试 SSH 连接**
   ```bash
   ssh -T git@github.com
   ```

## 十、总结

通过以上步骤，你可以将项目成功上传到 GitHub。如果遇到任何问题，可以参考 GitHub 官方文档或搜索相关解决方案。

祝项目上传顺利！