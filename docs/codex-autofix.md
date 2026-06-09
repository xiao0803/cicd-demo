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

同时确认 GitHub Actions 有权限创建 pull request：

1. 进入 `Settings` -> `Actions` -> `General`。
2. 在 `Workflow permissions` 中允许 read and write permissions。
3. 如果你的组织策略要求，启用 `Allow GitHub Actions to create and approve pull requests`。

如果没有配置 `OPENAI_API_KEY`，自动修复工作流会直接退出，不会修改任何内容。
