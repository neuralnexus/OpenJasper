#!/bin/sh
# Copyright VMware, Inc.
# SPDX-License-Identifier: APACHE-2.0
#
# ctlscript.sh interface for gonit
#

# Allow only root execution
if [ `id|sed -e s/uid=//g -e s/\(.*//g` -ne 0 ]; then
    echo "This script requires root privileges"
    exit 1
fi

help() {
	echo "usage: $0 help"
  echo "       $0 (start|stop|restart|status)"
  echo "       $0 (start|stop|restart|status) [service]"
	cat <<EOF

help       - this screen
start      - start the service(s)
stop       - stop  the service(s)
restart    - restart or start the service(s)
status     - show the status of the service(s)

EOF
}

get_status_from_gonit() {
  gonit summary $1 | awk '
BEGIN { IGNORECASE=1 }
/Process/ {
  printf $2 " "
  if ( $NF == "Running" ) {
    print "already running"
  }
  else {
    print "not running"
  }
}
'
}

if [ "x$1" = "help" ]; then
  help
elif [ "x$2" = "x" ]; then
  # Operation over all the services
  if [ "x$1" = "xstart" ] || [ "x$1" = "xstop" ]  || [ "x$1" = "xrestart" ]; then
    echo "$1ing services.." | sed 's/^./\u&/g'
    if [ -x /bin/systemctl ]; then
      /bin/systemctl $1 bitnami
    else
      /etc/init.d/bitnami $1
    fi
    exit $?
  elif [ "x$1" = "xstatus" ]; then
    get_status_from_gonit
  else
    help
    exit 1
  fi
else
  if [ "x$1" = "xstart" ] || [ "x$1" = "xstop" ]  || [ "x$1" = "xrestart" ]; then
    gonit $1 $2
    exit $?
  elif [ "x$1" = "xstatus" ]; then
    get_status_from_gonit $2
  else
    help
    exit 1
  fi
fi
