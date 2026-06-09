# GitHub Actions 的 Codex 自动修复

本仓库包含 `.github/workflows/codex-autofix.yml`。这个工作流参考了
OpenAI Cookbook 中的做法：当 CI 失败时，使用 Codex 自动分析失败原因并提交修复 PR。

## 工作原理

1. 常规的 `CI` 工作流会在 push 和 pull request 时运行。
2. 如果 `CI` 以 `failure` 状态结束，`Codex Auto-Fix on CI Failure` 会自动启动。
3. 自动修复工作流会检出失败的 commit，并把失败工作流日志保存到 `.codex-autofix/failed-run.log`。
4. `openai/codex-action` 会读取仓库代码和失败日志，生成最小必要修复，并运行 `mvn -B -ntp test`。
5. 如果验证通过，`peter-evans/create-pull-request` 会把修复提交成一个指向失败分支的 PR。

这个工作流只会处理当前仓库内分支的失败，不会处理 fork pull request。这样可以避免把
`OPENAI_API_KEY` 暴露给不受信任的代码。

## 必要配置

添加一个名为 `OPENAI_API_KEY` 的仓库 secret：

1. 打开 GitHub 仓库。
2. 进入 `Settings` -> `Secrets and variables` -> `Actions`。
3. 添加 `OPENAI_API_KEY`，值为允许 CI 使用的 OpenAI API key。

如果希望 Codex 可以修复 `.github/workflows/*.yml` 这类 GitHub Actions 工作流文件，还需要添加
`CODEX_AUTOFIX_TOKEN`：

1. 创建一个 GitHub Personal Access Token。
2. 如果使用 classic token，需要授予 `repo` 和 `workflow` scope。
3. 如果使用 fine-grained token，需要允许当前仓库的 `Contents`、`Pull requests` 和 `Workflows`
   读写权限。
4. 在仓库的 `Settings` -> `Secrets and variables` -> `Actions` 中添加
   `CODEX_AUTOFIX_TOKEN`，值为这个 token。

原因是 GitHub 默认的 `GITHUB_TOKEN` 即使具备 `contents: write`，也不能推送包含 workflow
文件变更的 commit。没有 `CODEX_AUTOFIX_TOKEN` 时，普通代码修复仍然可以创建 PR；但如果 Codex
修改了 `.github/workflows/` 下的文件，推送 PR 分支会被 GitHub 拒绝。

同时确认 GitHub Actions 有权限创建 pull request：

1. 进入 `Settings` -> `Actions` -> `General`。
2. 在 `Workflow permissions` 中允许 read and write permissions。
3. 如果你的组织策略要求，启用 `Allow GitHub Actions to create and approve pull requests`。

如果没有配置 `OPENAI_API_KEY`，自动修复工作流会直接退出，不会修改任何内容。
