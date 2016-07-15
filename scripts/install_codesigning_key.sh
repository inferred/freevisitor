#!/usr/bin/env bash
set -e
set -x

openssl aes-256-cbc -K ${encrypted_e90cbdc44364_key:?not set} -iv ${encrypted_e90cbdc44364_iv:?not set} -in codesigning.asc.enc -out codesigning.asc -d
gpg --fast-import -q codesigning.asc
