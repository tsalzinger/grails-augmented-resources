package at.scheinecker.grails.plugins.augmentedresources

import org.apache.commons.logging.LogFactory
import org.grails.plugin.resource.mapper.MapperPhase
/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 20/06/13
 * Time: 21:42
 * To change this template use File | Settings | File Templates.
 */
class AugmentResourceMapper {
	private static final LOG = LogFactory.getLog(this)

	def grailsApplication

	def resourceService

	def phase = MapperPhase.GENERATION

	// -1 to ensure running before lesscss resources
	def priority = -1

	static defaultIncludes = ['less/**/*.less', 'css/**/*.css', 'js/**/*.js']

	def map(resource, config) {

		if (!config.augment.containsKey(resource.sourceUrl)) {
			return // nothing to be done here
		}

		debug "Preparing to augment ${resource.sourceUrl}"

		def augmentConfig = config.augment[resource.sourceUrl]

		File before = getFile(augmentConfig.before)
		File after = getFile(augmentConfig.after)

		if (!before?.exists() && !after?.exists()) {
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

		if (before?.exists()) {
			debug "Copy contents of ${before}"
			before.findAll().each {
				sb << "${it}\n"
			}
		}

		debug "Copy contents of ${origin}"
		origin.findAll().each {
			sb << "${it}\n"
		}

		if (after?.exists()) {
			debug "Copy contents of ${after}"
			after.findAll().each {
				sb << "${it}\n"
			}
		}

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
