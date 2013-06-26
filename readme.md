#AUGMENTED RESOURCES GRAILS PLUGIN#
This plugin provides the possibility to extend any textual resource with any other resources - all via some simple configuration values.

##IMPORTAT NOTE##
**THIS PLUGIN IS STILL UNDER DEVELOPMENT AND NOT YET PUBLISHED - BUT FEEL FREE TO TRY IT OUT AND LET ME KNOW WHAT YOU THINK**

##USAGE EXAMPLE##

Imagine you have to build a grails application which has to support certain styling customizations (eg. adaption of colors to comply with a certain CI).
This is easily done with the help of this plugin! Just do your initial styling with a css preprocessor like less and define everything you want to be customizable in variables - eg some color like @font-color, @background-color, etc.
In addition just add some minor configurations like that:

```groovy
grails.resources.mappers.augment.lesscsscompatibility = true
grails.resources.mappers.augment.augment = [
	'less/main.less' : [
			after: "${System.properties['catalina.base']}/style/ci_modifications.less"
	]
]
```

Now you can deploy your application with a default style which works out of the box, but probably doesn't really integrate very well with most CIs.
The configured file (in our case /style/ci_modifications.less within the tomcat directory) doesn't have to exist - the plugin just doesn't do anything in that case.
But as soon as you provide that particular file it is appended to your main.less file - providing you with the possibility to override any variable definitions and add new selectors which all have full access to all available variables and mixins you have defined in your application.
By doing so you can easily provide a customized look & feel within a few minutes - all you need to do is create a single file and restart your server.

##CURRENT FEATURES##

* add content to any text based resource (eg. js, css, less, etc.) via simple configuration
* choose between prepending ('before') and appending ('after') the content to the file - or do both!

##CONFIGURATION##

The following configurations can be made atm:
###grails.resources.mappers.augment.includes###
A list of ant patterns of all resources to include into the augmentation process. Defaults to ['less/\*\*/\*.less*, 'css/\*\*/\*.css', 'js/\*\*/\*.js']

###grails.resources.mappers.augment.excludes###
A list of ant patterns of all resources for exclude form the augmentation process. Empty by default

###grails.resources.mappers.augment.lesscsscompatibility###
If true all augmented *.less* files will update the sourceUrl to enable processing by the lesscss plugin

###grails.resources.mappers.augment.augment###
A map containing the configuration on which resources to augment with which files.
The keys of the map are ant patterns against all resources which are included for processing are matched.
The values again are a map which can contain values for 'before' (prepend to the resource) and 'after' (append to the resource).
The values of this map correspond either to absolute file system paths or relative to the parent context of the grails application.

This leads to the following structure:

```groovy
['<<antPattern>>' :
    [
        before: '<<absoluteOrRelativePath>>',
        after: '<<absoluteOrRelativePath>>'
    ]
]
```

##KNOWN LIMITATIONS##

* the contets of the original files are copied and saved as a completly new file - therefore relative references will not work! (eg. in the usage example above you cant put ci_colors.less next to the ci_modifications.less and add a @import "ci_colors.less" to your file - it just wouldn't work!

##TODO##

* get rid of 'grails.resources.mappers.augment.lesscsscompatibility'
* add possbility to provide a list of files rather then single files
* add possbility to configure a resource provider instead of only a path to files
