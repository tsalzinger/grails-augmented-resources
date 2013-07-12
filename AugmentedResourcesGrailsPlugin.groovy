class AugmentedResourcesGrailsPlugin {
    // the plugin version
    def version = "1.0.RC1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.2 > *"
	// the other plugins this plugin depends on
	def dependsOn = [resources:'1.1.6 > *']
	def loadAfter = ['resources']
	// resources that are excluded from plugin packaging
	def pluginExcludes = [
			"grails-app/views/*.gsp",
			"web-app/less/*",
			"web-app/images/*",
			"web-app/css/*",
			"web-app/js/*"
	]

    def title = "Augmented Resources" // Headline display name of the plugin
    def author = "Thomas Scheinecker"
    def authorEmail = "tscheinecker@gmail.com"
    def description = '''\
This plugin enables you to extend all your resources with additional content.
This is especially useful when using it in conjunction with css pre-processors as you
can easily redefine variables or add complete styles without the need to rebuild your project.
'''

    def documentation = "http://grails.org/plugin/grails-augmented-resources-plugin"

    def license = "APACHE"

    def issueManagement = [ system: "github", url: "https://github.com/tscheinecker/grails-augmented-resources/issues" ]

    def scm = [ url: "https://github.com/tscheinecker/grails-augmented-resources" ]
}
