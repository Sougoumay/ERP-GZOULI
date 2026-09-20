#!/usr/bin/env bash
# PreToolUse hook: blocks any tool (Read, Bash, Grep, Glob, ...) from touching .env* files.
# The permissions.deny rules only cover the Read tool; this closes the Bash/Grep/Glob bypass.
input=$(cat)
target=$(echo "$input" | jq -r '[.tool_input.file_path, .tool_input.path, .tool_input.pattern, .tool_input.command] | map(select(. != null)) | join(" ")')

if echo "$target" | grep -qiE '(^|[[:space:]/"'"'"'])\.env([./"'"'"'[:space:]]|$)'; then
  echo '{"hookSpecificOutput":{"hookEventName":"PreToolUse","permissionDecision":"deny","permissionDecisionReason":"Blocked by policy: this command/path touches a .env file."}}'
fi
exit 0
