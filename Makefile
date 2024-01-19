
debug:
	sh buildDebug.sh

module:
	sh buildModule.sh


debugPlugin:
	./gradlew :test-module:module-user:assembleDebug --no-daemon -Dorg.gradle.debug=true -Pkotlin.compiler.execution.strategy=in-process

hub:
	sh buildHub.sh