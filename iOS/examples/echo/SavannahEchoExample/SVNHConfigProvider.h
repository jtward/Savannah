/*!
 * A ConfigProvider provides Savannah configuration given a URL.
 */
@protocol SVNHConfigProvider <NSObject>

@required

/*!
 * Returns true if Savannah should be provided for the page at the given URL.
 * @param url the URL to test
 * @return true if Savannah should be provided for the page at the given URL, false otherwise.
 */
- (BOOL) shouldProvideSavannahForURL:(NSURL *)url;

/*!
 * Returns the array of plugins to be provided for the page at the given URL.
 * @param url the URL to test.
 * @return the array of plugins to be provided for the page at the given URL.
 */
- (NSArray *) pluginsForURL:(NSURL *)url;

/*!
 * Returns the settings to be provided for the page at the given URL.
 * @param url the URL to test.
 * @return the settings to be provided for the page at the given URL.
 */
- (NSDictionary *) settingsForURL:(NSURL *)url;

@end