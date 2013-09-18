### Makefile
### author: mikey

include ../misc/includes.mk

VTCLIENT_LIBS = \
	libs/volley/volley.jar \
	libs/gson/gson-2.2.4-repackaged.jar

# Misc config

ANT_CLASSJAR = bin/classes.jar
BUILD_DIR := build
VENMO_TOUCH_JAR := $(BUILD_DIR)/venmo-touch.jar
VENMO_TOUCH_JAVADOC_JAR := $(BUILD_DIR)/venmo-touch-javadoc.jar

###

all: $(VENMO_TOUCH_JAR)

$(BUILD_DIR):
	mkdir -p $@

$(ANT_CLASSJAR):
	# Ant build.
	@ant clean debug

$(VENMO_TOUCH_JAR): $(BUILD_DIR) $(VTCLIENT_LIBS) $(ANT_CLASSJAR)
	# Stage all compiled classes.
	@rm -rf $(BUILD_DIR)/tmp
	@mkdir $(BUILD_DIR)/tmp
	@for jar in $(VTCLIENT_LIBS) $(ANT_CLASSJAR); do \
	  unzip -qq -o -d $(BUILD_DIR)/tmp $$jar; \
	done

	# JARify.
	@cd $(BUILD_DIR)/tmp && \
	   $(JAR) cf vt.jar `find . -name "*.class"`

	# Finish up.
	mv $(BUILD_DIR)/tmp/vt.jar $@
	@rm -rf $(BUILD_DIR)/tmp
	@echo "--> $@"

clean:
	# Ant clean.
	@ant clean
	# Delete build dir.
	@rm -rf $(BUILD_DIR)

.PHONY: all clean
.DEFAULT: all
