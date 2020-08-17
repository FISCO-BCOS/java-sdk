#!/bin/bash

set -e

scan_code_script="cobra/cobra.py -f json -o /tmp/report.json -t "
ignore_files=(sh crt key json toml SignatureTest.java)
commit_limit=6

LOG_ERROR() {
    content=${1}
    echo -e "\033[31m${content}\033[0m"
}

LOG_INFO() {
    content=${1}
    echo -e "\033[32m${content}\033[0m"
}

should_ignore() {
    local file=${1}
    for ignore in ${ignore_files[*]}; do
        if echo "${file}" | grep "${ignore}" &>/dev/null; then
            echo "ignore ${file} ${ignore}"
            return 0
        fi
    done
    return 1
}

scan_code() {
    # Redirect output to stderr.
    exec 1>&2
    for file in $(git diff-index --name-status HEAD^ | awk '{print $2}'); do
        if should_ignore "${file}"; then continue; fi
        if [ ! -f "${file}" ]; then continue; fi
        LOG_INFO "check file ${file}"
        python ${scan_code_script} "$file"
        trigger_rules=$(jq -r '.' /tmp/report.json | grep 'trigger_rules' | awk '{print $2}' | sed 's/,//g')
        echo "trigger_rules is ${trigger_rules}"
        rm /tmp/report.json
        if [ ${trigger_rules} -ne 0 ]; then
            echo "######### ERROR: Scan code failed, please adjust them before commit"
            exit 1
        fi
    done
}

install_cobra() {
    git clone https://github.com/WhaleShark-Team/cobra.git
    pip install -r cobra/requirements.txt
    cp cobra/config.template cobra/config
}

check_commit_message()
{
    local commits=$(git rev-list --count HEAD^..HEAD)
    if [ ${commit_limit} -lt ${commits} ]; then
        LOG_ERROR "${commits} commits, limit is ${commit_limit}"
        exit 1
    fi
    local unique_commit=$(git log --format=%s HEAD^..HEAD | sort -u | wc -l)
    if [ ${unique_commit} -ne ${commits} ]; then
        LOG_ERROR "${commits} != ${unique_commit}, please make commit message unique!"
        exit 1
    fi
    local merges=$(git log --format=%s HEAD^..HEAD | grep -i merge | wc -l)
    if [ ${merges} -gt 2 ]; then
        LOG_ERROR "PR contain merge : ${merges}, Please rebase!"
        exit 1
    fi
}

check_commit_message
install_cobra
scan_code
