# getNexusVersions

Helper class to query a Nexus v3.x servers via the REST API to return the current assets in a given repository.

There are some assumptions built into this shared library.

 * Expecting `raw` or `maven` type repositories only
 * when searching `raw` only returns assets of type *.zip
 * the filterPattern must be a valid regular expression
