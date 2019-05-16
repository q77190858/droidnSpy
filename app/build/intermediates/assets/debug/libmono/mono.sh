#!/bin/sh
dirname $0
cd `dirname $0`
cd mono/bin
/data/data/com.juju.droidSpy/app_libmono/mono/bin/mono-sgen $@
