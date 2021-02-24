package com.kme.kaltura.kmesdk.content.whiteboard

import android.graphics.PointF
import com.kme.kaltura.kmesdk.ws.message.module.KmeWhiteboardModuleMessage
import com.kme.kaltura.kmesdk.ws.message.type.KmeWhiteboardBackgroundType

/**
 * An interface for whiteboard in the room
 */
interface IKmeWhiteboardListener {

    /**
     * Initialize function. Setting config.
     *
     * @param whiteboardConfig The metadata for whiteboard.
     */
    fun init(whiteboardConfig: KmeWhiteboardView.Config?)

    /**
     * Sets the list of drawings.
     *
     * @param drawings The list of drawings.
     */
    fun setDrawings(drawings: List<KmeWhiteboardModuleMessage.WhiteboardPayload.Drawing>)

    /**
     * Adds the drawing.
     *
     * @param drawing The drawing containing a path object with all the data needed for drawing.
     */
    fun addDrawing(drawing: KmeWhiteboardModuleMessage.WhiteboardPayload.Drawing)

    /**
     * Updates the whiteboard background.
     *
     * @param backgroundType The type of whiteboard background that needs to draw.
     */
    fun updateBackground(backgroundType: KmeWhiteboardBackgroundType?)

    /**
     * Updates the current position of the laser pointer. Show pointer if not already created.
     *
     * @param point The point that contains coordinates for the laser pointer.
     */
    fun updateLaserPosition(point: PointF)

    /**
     * Hide the laser pointer.
     */
    fun hideLaser()

    /**
     * Removes the existing drawing from the whiteboard view.
     *
     * @param layer The id of the drawing to be removed.
     */
    fun removeDrawing(layer: String)

    /**
     * Removes all drawings from the whiteboard view.
     */
    fun removeDrawings()

}