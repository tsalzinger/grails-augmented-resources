class AugmentedResourcesGrailsPlugin {
	def version = "1.0.1-SNAPSHOT"
	def grailsVersion = "2.0 > *"
	def loadAfter = ['resources']
	def pluginExcludes = [
		"grails-app/views/**",
		"web-app/**"
	]

	def title = "Augmented Resources"
	def author = "Thomas Scheinecker"
	def authorEmail = "tscheinecker@gmail.com"
	def description = '''\
Enables you to extend all your resources with additional content.
This is especially useful when using it in conjunction with css pre-processors as you
can easily redefine variables or add complete styles without the need to rebuild your project.
'''

	def documentation = "https://github.com/tscheinecker/grails-augmented-resources"

	def license = "APACHE"

	def issueManagement = [ system: "github", url: "https://github.com/tscheinecker/grails-augmented-resources/issues" ]

	def scm = [ url: "https://github.com/tscheinecker/grails-augmented-resources" ]
}
