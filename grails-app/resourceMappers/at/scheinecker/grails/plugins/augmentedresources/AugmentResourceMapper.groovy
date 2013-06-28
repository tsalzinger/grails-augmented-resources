package at.scheinecker.grails.plugins.augmentedresources

import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.grails.io.support.AntPathMatcher
import org.grails.plugin.resource.mapper.MapperPhase

/**
 * This resource mapper appends and prepends the content of files to configured resources.
 * @author Thomas Scheinecker
 */
class AugmentResourceMapper {
	private static final LOG = LogFactory.getLog(this)

	def grailsApplication

	def resourceService

	def phase = MapperPhase.GENERATION

	// -1 to ensure running before lesscss resources
	def priority = -1

	static defaultIncludes = ['less/**/*.less', 'css/**/*.css', 'js/**/*.js']

	AntPathMatcher antPathMatcher = new AntPathMatcher()

	private List<String> getMatchingPatterns(patterns, String sourceUrl) {
		List<String> matches = []

		String url = sourceUrl

		if (sourceUrl.startsWith('/')) {
			url = sourceUrl.substring(1)
		}

		patterns.each {
			if (antPathMatcher.match(it, url)) {
				matches << it
			}
		}

		return matches
	}

	private void copyAll(paths, StringBuilder sb) {
		paths.each {
			!it ?: copyContent(getFile(it), sb)
		}
	}


	private void copyContent(File src, StringBuilder sb) {
		if (src?.exists()) {
			debug "Copy contents of ${src}"
			src.findAll().each {
				sb << "${it}\n"
			}
		}
	}

	private add(list, toadd) {
		if (toadd) {
			if (toadd instanceof Collection) {
				list.addAll(toadd)
			} else {
				list << toadd
			}
		}
	}

	def map(resource, config) {

		List<String> patterns = getMatchingPatterns(config.augment.keySet(), resource.sourceUrl)

		if (!patterns) {
			return // nothing to be done here
		}

		debug "Preparing to augment ${resource.sourceUrl}"

		def prepends = []
		def appends = []

		patterns.each {
			Object augmentConfig = config.augment[it]

			add prepends, augmentConfig.prepend
			add appends, augmentConfig.append
		}

		if (!prepends && !appends) {
			return // nothing to be done here
		}

		File origin = resource.processedFile

		File augmentFolder = new File(resourceService.workDir as File, 'augmented')
		File targetDir = new File(augmentFolder, resource.processedFileExtension)

		File target = new File(targetDir,
				replaceFileExtension(origin.name, "augmented.${resource.processedFileExtension}"))

		if (!targetDir.exists()) {
			debug "Creating new folder ${targetDir}"
			if (!targetDir.mkdirs()) {
				LOG.error "Failed to create folder ${targetDir} - resource augmentation aborted!"
				return
			}
		}

		if (target.exists()) {
			debug "${target.path} alread existst - trying to delete"
			if (!target.delete()) {
				LOG.error "Failed to delete ${target} - resource augmentation aborted!"
				return
			}
		}

		StringBuilder sb = new StringBuilder()

		!prepends ?: debug("Prepending...")
		copyAll(prepends, sb)

		debug("Copy original...")
		copyContent(origin, sb)

		!appends ?: debug("Appending...")
		copyAll(appends, sb)

		debug "Writing augmented file content to ${target}"
		target << sb.toString()

		resource.processedFile = target
		resource.updateActualUrlFromProcessedFile()

		if (config.lesscsscompatibility && resource.processedFileExtension == 'less') {
			// this part is completely messed up and is only here because the lesscss resources plugin
			// uses the sourceUrl instead of the processed file ...
			File original = getOriginalFile(resource.sourceUrl)
			File copy = new File(original.parentFile, target.name)
			if (copy.exists()) {
				debug "Compatiblilty file ${copy} already exists - trying to delete"
				if (!copy.delete()) {
					LOG.error "Compatibility file couldn't be deleted - aborting"
					return
				}
			}

			debug "Writing augmented file content to compatibility file ${target}"
			copy << sb.toString()
			resource.sourceUrl = "${resource.sourceUrl.replaceAll(original.name, target.name)}"
		}
	}

	private String replaceFileExtension(String fileName, String extension) {
		String withoutExtension = fileName.subSequence(0, fileName.lastIndexOf('.'))
		return "${withoutExtension}.${extension}"
	}

	private File getFile(path) {
		return path ? grailsApplication.parentContext.getResource(path).file : null
	}

	private static void debug(String message) {
		!LOG.debugEnabled ?: LOG.debug(message)
	}

	private File getOriginalFile(String path) {
		return grailsApplication.parentContext.getResource(path).file
	}
}
