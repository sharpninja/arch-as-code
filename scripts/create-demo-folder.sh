#!/usr/bin/env bash

export PS4='+${BASH_SOURCE}:${LINENO}:${FUNCNAME[0]:+${FUNCNAME[0]}():} '

repo_dir="$(git rev-parse --show-toplevel)"
aac_dir=/tmp/aac/demo-folder

set -e
set -u
set -o pipefail

function print-usage() {
    echo "Usage: $0 [-C|--cleanup][-h|--help]"
}

function print-help() {
    cat <<EOH
This script will:
 - run ./gradlew build (not clean)
 - create a demo folder where it's easy to execute the binary
 - that folder will be as if 'init' and 'au init' had already been run

Options:
   -h, --help       Print this help and exit
EOH
}

function maybe-setup-git() {
    [[ -d .git ]] && return

    run git init
    run git add .
    run git commit -m Init
}

function maybe-create-init-au-yaml() {
    # ASSUMES running from within the create demo folder, not the repo dir
    for br in $(git for-each-ref --format='%(refname:short)'); do
        case $br in
        test) return ;; # If there's already a branch, don't change it
        esac
    done

    run git checkout -q -b test
    mkdir -p architecture-updates/test
    cp "$repo_dir/documentation/products/arch-as-code/architecture-updates/show-tdd-in-diff/architecture-update.yml" architecture-updates/test
    git add .
    git commit -q -m 'Set up demonstration AU'
    # TODO: The script leaves you in 'test' branch if exists, but in 'master'
    #       if copying in demo files
    run git checkout -q master
}

# shellcheck disable=SC1090
. "${0%/*}/colors.sh"
export TTY=false
[[ -t 1 ]] && TTY=true

# A trick to show output on failure, but not on success
outfile="/tmp/out"

# Note: STDOUT and STDERR may be mixed.  This function does not attempt to
# address this: STDERR will always appear before STDOUT using this function
function run() {
    "$@" >"$outfile" || {
        local rc=$?
        cat "$outfile"
        return $rc
    }
}

# shellcheck disable=SC2214
while getopts :h-: opt; do
    [[ $opt == - ]] && opt=${OPTARG%%=*} OPTARG=${OPTARG#*=}
    case $opt in
    h | help)
        print-help
        exit 0
        ;;
    *)
        print-usage
        exit 2
        ;;
    esac
done
shift $((OPTIND - 1))

# ASSUMES script is run from somewhere within the project repo
repo_dir="$(git rev-parse --show-toplevel)"
cd "$repo_dir"

echo "Working..."

mkdir -p "$aac_dir"

if [[ -e "$aac_dir"/.arch-as-code ]]; then
    : # Keep existing
elif [[ -d ~/.arch-as-code ]]; then
    ln -s ~/.arch-as-code "$aac_dir"
elif [[ -d "$repo_dir"/.arch-as-code ]]; then
    ln -s ~/.arch-as-code "$repo_dir"/.arch-as-code
else
    echo "$0: No $HOME/.arch-as-code configuration files" >&2
    exit 1
fi

rm -rf $aac_dir/.install
mkdir -p $aac_dir/.install

# TODO: Create script as `aac` rather than use an alias
cat <<EOF >"$aac_dir"/.gitignore
.install/
# This is a symlink to your AaC configs in home or project dir
.arch-as-code
# This is a local symlink for execution
/arch-as-code
EOF
cat <<EOF >"$aac_dir"/.java-version
11
EOF
mkdir -p "$aac_dir"/.install/bin

run ./gradlew bootJar
cp ./build/libs/arch-as-code-*.jar $aac_dir/.install/bin

cat <<EOS >$aac_dir/.install/bin/arch-as-code
#!/bin/sh

# The extra flag is to quiet WARNINGS from Jackson
exec java --illegal-access=permit -jar "$aac_dir"/.install/bin/arch-as-code-*.jar "\$@"
EOS
chmod a+rx $aac_dir/.install/bin/arch-as-code

cd $aac_dir

maybe-setup-git

# shellcheck disable=SC2016
ln -fs .install/bin/arch-as-code .

# Install a sample
[[ -f product-architecture.yml ]] \
    || cp "$repo_dir"/documentation/products/arch-as-code/product-architecture.yml .

maybe-create-init-au-yaml

cat <<EOM
${pbold}Demo folder created in '$PWD'.${preset}
Change to that directory, and use ./arch-as-code or "aac" alias.
(Once there, you may find 'alias aac=\$PWD/arch-as-code' helpful.)
This is setup as a Git repo (or there was already one present).
If there were already a '$PWD' directory, we overwrite the AaC
parts only.
If there were already a 'test' branch for architecture updates, we left it be.
If there were already a '.arch-as-code/' directory, we left it be, else we
linked to the one in your home directory or your project root, if present.
EOM
